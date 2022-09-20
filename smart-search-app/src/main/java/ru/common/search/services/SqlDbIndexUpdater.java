package ru.common.search.services;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.common.search.index.utils.IndexWrapper;
import ru.common.search.services.utils.AsyncIndexWriter;
import ru.common.search.services.utils.SqlBaseReader;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class SqlDbIndexUpdater {
    static final int STEPS = 100;

    private final IndexWrapper indexWrapper;
    private final Executor executor;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SqlDbIndexUpdater(
            IndexWrapper indexWrapper,
            @Qualifier("appExecutor") Executor executor,
            JdbcTemplate jdbcTemplate
    ) {
        this.indexWrapper = indexWrapper;
        this.executor = executor;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Async("appExecutor")
    public void updateIndex() {
        LinkedBlockingQueue<Document> queue = new LinkedBlockingQueue<>();
        final RAMDirectory index = new RAMDirectory();
        try {
            Long minIdBase = getMinId();
            Long maxIdBase = getMaxId();
            final long step = (maxIdBase - minIdBase) / STEPS;

            AsyncIndexWriter asyncIndexWriter = new AsyncIndexWriter(queue, index);
            executor.execute(asyncIndexWriter);

            CountDownLatch latch = new CountDownLatch(STEPS + 1);

            long start = System.currentTimeMillis();
            for (int i = 0; i < (STEPS + 1); i++) {
                long minId = minIdBase + (i * step);
                executor.execute(new SqlBaseReader(
                        minId, minId + step, queue, latch, jdbcTemplate));
            }

            latch.await();

            while (queue.size() > 0) {
                Thread.sleep(1000);
                System.out.println("awaiting async writer finished queue=" + queue.size());
            }

            System.out.println("OK");
            asyncIndexWriter.setWork(false);
            long allRecordCount = getCountFromInterval(minIdBase, maxIdBase);
            System.out.println("OK counter=" + asyncIndexWriter.getCounter() + " detected in DB:" + allRecordCount + "   time = " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        indexWrapper.updateIndex(index);
    }

    private Long getMinId() {
        return getSrviceId("SELECT MIN(ID) AS MIN_ID FROM USER_TABLE");
    }

    private Long getMaxId() {
        return getSrviceId("SELECT MAX(ID) AS MAX_ID FROM USER_TABLE");
    }

    private Long getSrviceId(String query) {
        return jdbcTemplate.queryForObject(query, new Object[]{}, (rs, rowNum) -> rs.getLong(1));
    }

    private long getCountFromInterval(long minId, long maxId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USER_TABLE WHERE ID BETWEEN ? AND ?", new Object[]{minId, maxId}, (rs, rowNum) -> rs.getLong(1));
    }

}
