package com.example.event;

import com.example.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractEvent {
    private final long timestamp;
    private final String id;
    private final String host;
    @JsonProperty("api_response")
    private ApiResponse apiResponse;

    public AbstractEvent(String id, String host) {
        this(id, host, null);
    }

    public AbstractEvent(String id, String host, ApiResponse apiResponse) {
        this(System.currentTimeMillis(), id, host, apiResponse);
    }

    @JsonCreator
    public AbstractEvent(long timestamp, String id, String host, ApiResponse apiResponse) {
        this.timestamp = timestamp;
        this.id = id;
        this.host = host;
        this.apiResponse = apiResponse;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public ApiResponse getApiResponse() {
        return apiResponse;
    }

    public void setApiResponse(ApiResponse apiResponse) {
        this.apiResponse = apiResponse;
    }

    @Override
    public String toString() {
        return "AbstractEvent{" +
                "timestamp=" + timestamp +
                ", id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", apiResponse=" + apiResponse +
                '}';
    }
}
