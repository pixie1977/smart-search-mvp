package ru.common.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchRequest {

    @JsonProperty("count")
    private int count;
    @JsonProperty("from")
    private int from;
    @JsonProperty("field-name")
    private String fieldName;
    @JsonProperty("query")
    private String query;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
