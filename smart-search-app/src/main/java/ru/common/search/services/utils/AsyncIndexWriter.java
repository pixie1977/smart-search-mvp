package ru.common.search.services.utils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public class AsyncIndexWriter implements Runnable{
    private final Queue<Document> queue;
    private final IndexWriter indexWriter;
    private final RAMDirectory index;

    private final AtomicLong counter = new AtomicLong();

    volatile boolean work = true;

    public AsyncIndexWriter(Queue<Document> queue, RAMDirectory index) {
        this.queue = queue;
        this.index = index;
        try {
            this.indexWriter = new IndexWriter(index, new IndexWriterConfig(new StandardAnalyzer()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (work || !queue.isEmpty()){
            try {
                while (work || queue.size() > 0){
                    Document document;
                    while (queue.size() > 0){
                        if((document = queue.poll()) != null){
                            indexWriter.addDocument(document);
                            counter.incrementAndGet();
                        }
                    }
//                    indexWriter.commit();
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("Commiting index");
                    indexWriter.commit();
                    indexWriter.close();
                    System.out.println("OK");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    public Long getCounter() {
        return counter.get();
    }
}
