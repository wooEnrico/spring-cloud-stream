package com.example.event;

import com.example.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonCreator;

public class AbstractEvent {
    private final String id;
    private final String host;
    private ApiResponse apiResponse = new ApiResponse();

    public AbstractEvent(String id, String host) {
        this.id = id;
        this.host = host;
    }

    @JsonCreator
    public AbstractEvent(String id, String host, ApiResponse apiResponse) {
        this.id = id;
        this.host = host;
        this.apiResponse = apiResponse;
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

    @Override public String toString() {
        return "AbstractEvent{" +
                "id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", apiResponse=" + apiResponse +
                '}';
    }
}
