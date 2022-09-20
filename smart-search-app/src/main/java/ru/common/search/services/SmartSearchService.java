package ru.common.search.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.automaton.LevenshteinAutomata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.common.data.dto.User;
import ru.common.search.index.utils.IndexWrapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartSearchService {
    private static Logger LOG = LoggerFactory.getLogger(SmartSearchService.class);
    public static final String ERROR_EXTRACTING_DOC_FROM_SCORE_DOC = "error extracting doc from ScoreDoc";

    int MAX_RESULTS = 1000;
    final IndexWrapper indexWrapper;

    @Autowired
    public SmartSearchService(IndexWrapper indexWrapper) {
        this.indexWrapper = indexWrapper;
    }

    public List<Map<String,String>> findByStrictValue(String sQuery, int from, int pageSize){
        try{
            IndexReader indexReader = DirectoryReader.open(indexWrapper.getIndex());
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser("", analyzer);
            Query query = parser.parse(sQuery);
            if(pageSize<=0){
                pageSize = MAX_RESULTS;
            }
            TopDocs topDocs = indexSearcher.search(query, pageSize);
            return getDocs(from, pageSize, indexSearcher, topDocs);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, String>> findByFuzzyValue(String sQuery, int from, int pageSize) {
        try {
            final int maxEdits = 2;
            IndexReader indexReader = DirectoryReader.open(indexWrapper.getIndex());
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            if (pageSize <= 0) {
                pageSize = MAX_RESULTS;
            }
            BooleanQuery.Builder bool = new BooleanQuery.Builder();

            List<String> userFieldNames = User.getFieldNames();
            userFieldNames.forEach(fName -> {
                FuzzyQuery fuzzy = new FuzzyQuery(new Term(fName, sQuery.toLowerCase()), LevenshteinAutomata.MAXIMUM_SUPPORTED_DISTANCE);
                bool.add(fuzzy, BooleanClause.Occur.SHOULD);
            });

            TopDocs topDocs = indexSearcher.search(bool.build(), pageSize);
            List<Map<String, String>> resList = getDocs(from, pageSize, indexSearcher, topDocs);
            return resList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Map<String, String>> getDocs(int from, int pageSize, IndexSearcher indexSearcher, TopDocs topDocs) {
        return Arrays.stream(topDocs.scoreDocs)
                .skip(from)
                .limit(pageSize)
                .parallel()
                .map(scoreDoc -> {
                    try {
                        return indexSearcher.doc(scoreDoc.doc);
                    } catch (Exception e) {
                        LOG.error(ERROR_EXTRACTING_DOC_FROM_SCORE_DOC, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(doc -> {
                    Map<String, String> resMap = new HashMap<>();
                    doc.getFields().forEach(field -> resMap.put(field.name(), field.stringValue()));
                    return resMap;
                })
                .collect(Collectors.toList());
    }

    public Long getCount(){ return Long.valueOf(findByStrictValue("*:*",0,Integer.MAX_VALUE).size());}
}
