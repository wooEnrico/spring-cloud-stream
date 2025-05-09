package com.example.future;

import java.util.concurrent.CompletableFuture;

public class DataCompletableFuture<D, T> extends CompletableFuture<T> {

    private final D data;

    public DataCompletableFuture(D data) {
        this.data = data;
    }

    public D getData() {
        return data;
    }
}
