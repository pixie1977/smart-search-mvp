package ru.common.search.services.utils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class SqlBaseReader implements Runnable{
    private static final String PASSWORD_FIELD_NAME = "password";
    private static final AtomicLong ID_GEN = new AtomicLong();
    private static final String GET_DATA_QUERY = "SELECT * FROM USER_TABLE WHERE ID BETWEEN ? AND ?";

    private final long minId;
    private final long maxId;
    private final Queue<Document> queue;
    private final CountDownLatch latch;
    private final JdbcTemplate jdbcTemplate;


    public SqlBaseReader(long minId, long maxId, Queue<Document> queue, CountDownLatch latch, JdbcTemplate jdbcTemplate) {
        this.minId = minId;
        this.maxId = maxId;
        this.queue = queue;
        this.latch = latch;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run() {
        List<Document> documentList = new ArrayList<>();
        try {
            documentList = jdbcTemplate.query(
                    GET_DATA_QUERY,
                    new Object[]{minId, maxId},
                    new RowMapper() {
                        @Nullable
                        @Override
                        public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                            Document document = new Document();
                            document.add(new TextField("lucene-id",
                                    String.valueOf(ID_GEN.getAndIncrement()),
                                    Field.Store.YES));
                            int columns = resultSet.getMetaData().getColumnCount();
                            for (int j = 1; j <= columns; j++) {
                                String name = resultSet.getMetaData().getColumnName(j);
                                if(PASSWORD_FIELD_NAME.equals(name)){
                                    continue;
                                }
                                String value = resultSet.getString(j);
                                if (value == null) {
                                    value = "";
                                }
                                document.add(new TextField(name, value, Field.Store.YES));
                            }
                            return document;
                        }
                    });
            queue.addAll(documentList);
        } finally {
            latch.countDown();
            System.out.println("Left = " + latch.getCount() + " sqlreaded = " + documentList.size() + " in queue:" + queue.size());
        }
    }
}
