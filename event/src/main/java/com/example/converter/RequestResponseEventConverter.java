package com.example.converter;

import com.example.event.AbstractEvent;
import com.example.response.ApiResponse;

import java.util.concurrent.CompletableFuture;

public interface RequestResponseEventConverter {

    /**
     * Process the request event and return a CompletableFuture of AbstractEvent.
     *
     * @param event the event to process
     * @return a CompletableFuture of ApiResponse
     */
    CompletableFuture<ApiResponse> request(AbstractEvent event);

    /**
     * Process the response event.
     *
     * @param event the event to process
     * @return a CompletableFuture of ApiResponse
     */
    CompletableFuture<ApiResponse> response(AbstractEvent event);
}
