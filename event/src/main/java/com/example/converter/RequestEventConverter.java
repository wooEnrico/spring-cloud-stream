package com.example.converter;

import com.example.event.AbstractEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class RequestEventConverter {

    private final static ConcurrentHashMap<String, CompletableFuture<AbstractEvent>> userEventCache = new ConcurrentHashMap<>();

    public static CompletableFuture<AbstractEvent> processRequest(AbstractEvent event) {
        return userEventCache.computeIfAbsent(event.getId(), k -> {
            return new CompletableFuture<AbstractEvent>();
        });
    }

    public static void processResponse(AbstractEvent event) {
        CompletableFuture<AbstractEvent> future = userEventCache.remove(event.getId());
        if (future != null) {
            future.complete(event);
        }
    }
}
