package ru.common.search.index.utils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.BaseDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class IndexWrapper {
    private RAMDirectory index;
    private IndexReader indexReader;
    private IndexSearcher searcher;
    private final ReentrantLock locker = new ReentrantLock();

    @Autowired
    public IndexWrapper(){ updateIndex(null);}

    public void updateIndex(RAMDirectory newIndex){
        locker.lock();
        try{
            if(newIndex != null){
                index = newIndex;
            } else {
                index = new RAMDirectory();
                IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(new StandardAnalyzer()));
                writer.commit();
                writer.close();
            }
            indexReader = DirectoryReader.open(index);
            searcher = new IndexSearcher(indexReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public BaseDirectory getIndex(){
        locker.lock();
        try {
            return index;
        } finally {
            locker.unlock();
        }
    }
}
