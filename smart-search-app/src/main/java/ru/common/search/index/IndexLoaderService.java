package ru.common.search.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.TermRangeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.common.search.index.utils.IndexWrapper;
import ru.common.search.index.utils.MapUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class IndexLoaderService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexLoaderService.class);

    private final IndexWrapper indexWrapper;

    public IndexLoaderService(IndexWrapper indexWrapper) {
        this.indexWrapper = indexWrapper;
    }

    public long loadMapToIndex(Map<String, String> src) {
        return loadMapToIndex(Collections.singletonList(src));
    }

    public long loadMapToIndex(Collection<Map<String, String>> srcCollection) {
        List<Document> documents = srcCollection.stream()
                .map(MapUtils::mapObjToDocument)
                .collect(Collectors.toList());
        return loadDocumentsToIndex(documents);
    }

    private long loadDocumentsToIndex(List<Document> documents) {
        AtomicInteger faultCounter = new AtomicInteger(0);
        try {
            final IndexWriter writer = createWriter();
            documents.forEach(document -> {
                try {
                    writer.addDocument(document);
                } catch (IOException e) {
                    faultCounter.incrementAndGet();
                    LOG.error("error adding doc to writer", e);
                }
            });
            writer.commit();
            writer.close();
            return documents.size() - faultCounter.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long removeFromIndexByTimestamp(Long timestamp){
        try{
            final IndexWriter writer = createWriter();
            long deletedDocsCount = writer.deleteDocuments(
                    TermRangeQuery.newStringRange(
                            MapUtils.TIMESTAMP_FIELD,
                            "0",
                            String.valueOf(timestamp),
                            true,
                            true
                    )
            );
            writer.commit();
            writer.close();
            return deletedDocsCount;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long clearAll(Long timestamp){
        try{
            final IndexWriter writer = createWriter();
            long deletedDocsCount = writer.deleteAll();
            writer.commit();
            writer.close();
            return deletedDocsCount;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private IndexWriter createWriter() throws IOException {
        return new IndexWriter(indexWrapper.getIndex(), new IndexWriterConfig(new StandardAnalyzer()));
    }

}
