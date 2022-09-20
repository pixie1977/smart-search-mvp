package ru.common.search.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemoveResponse implements Serializable{

    @JsonProperty("error")
    private String error;
    @JsonProperty("success-count")
    private Long successCount;
    @JsonProperty("fault-count")
    private Long faultCount;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getFaultCount() {
        return faultCount;
    }

    public void setFaultCount(Long faultCount) {
        this.faultCount = faultCount;
    }

}
