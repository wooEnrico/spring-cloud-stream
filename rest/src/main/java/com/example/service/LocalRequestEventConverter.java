package com.example.service;

import com.example.converter.RequestResponseEventConverter;
import com.example.event.AbstractEvent;
import com.example.future.DataCompletableFuture;
import com.example.response.ApiResponse;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class LocalRequestEventConverter implements RequestResponseEventConverter, InitializingBean, DisposableBean {

    private final ConcurrentHashMap<String, DataCompletableFuture<AbstractEvent, ApiResponse>> CACHE = new ConcurrentHashMap<>(10000);
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
    private final long requestTimeout;

    @Autowired
    public LocalRequestEventConverter(@Value("${http.request.timeout.mill.seconds:30000}") long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Schedule cleanup task to run every minute
        this.cleanupExecutor.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            this.CACHE.entrySet().removeIf(entry -> removeIf(currentTime, entry));
        }, 10, 1, TimeUnit.SECONDS);
    }

    private boolean removeIf(long currentTime, Map.Entry<String, DataCompletableFuture<AbstractEvent, ApiResponse>> entry) {
        DataCompletableFuture<AbstractEvent, ApiResponse> future = entry.getValue();
        if (future.isDone() || future.isCancelled()) {
            return true;
        }

        AbstractEvent event = future.getData();
        if (event == null) {
            return true;
        }

        if (currentTime - event.getTimestamp() > this.requestTimeout) {
            ApiResponse.Builder builder = ApiResponse.builder()
                    .timestamp(event.getTimestamp())
                    .status(500)
                    .error("Request Timeout")
                    .message("Request timed out after " + this.requestTimeout + "ms");
            future.complete(builder.build());
            return true;
        }
        return false;
    }

    @Override
    public void destroy() throws Exception {
        this.cleanupExecutor.shutdown();
    }

    @Override
    public CompletableFuture<ApiResponse> request(AbstractEvent event) {
        return this.CACHE.computeIfAbsent(event.getId(), k -> new DataCompletableFuture<>(event));
    }

    @Override
    public CompletableFuture<ApiResponse> response(AbstractEvent event) {
        CompletableFuture<ApiResponse> future = this.CACHE.remove(event.getId());
        if (future != null) {
            future.complete(event.getApiResponse());
        }
        return future;
    }
}
