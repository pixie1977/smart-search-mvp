package ru.common.search.index.utils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MapUtils {

    public static final String ID_FIELD = "id";
    public static final String TIMESTAMP_FIELD = "timestamp";
    private static final AtomicLong ID_SUFFIX = new AtomicLong(0);

    private MapUtils(){}

    public static Document mapObjToDocument(Map mapDoc){
        final Document document = new Document();
        String millis = String.valueOf(System.currentTimeMillis());
        String id = String.join("",
                millis,
                String.valueOf(ID_SUFFIX.getAndIncrement())
        );
        document.add(new TextField(ID_FIELD, id, Field.Store.YES));
        document.add(new TextField(TIMESTAMP_FIELD, millis, Field.Store.YES));
        mapDoc.keySet().forEach(
                key -> document.add(new TextField(key.toString(), mapDoc.get(key).toString(), Field.Store.YES))
        );
        return document;
    }
}
