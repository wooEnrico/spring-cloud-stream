package com.example.controller;

import com.example.converter.RequestEventConverter;
import com.example.event.AbstractEvent;
import com.example.event.GetUserEvent;
import com.example.response.ApiResponse;
import com.example.service.RequestStream;
import com.example.utils.NetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/test")
public class TestController {

    private final RequestStream streamService;

    @Autowired
    public TestController(RequestStream streamService) {
        this.streamService = streamService;
    }

    @GetMapping("/user/{username}")
    public Mono<ApiResponse> getUser(@PathVariable("username") String username) {
        GetUserEvent getUserEvent = new GetUserEvent(UUID.randomUUID().toString(), NetUtil.getHostId(), username);

        final Sinks.EmitResult emitResult = streamService.publishEvent(getUserEvent);
        if (emitResult.isFailure()) {
            return Mono.error(new RuntimeException("Failed to publish event"));
        }
        CompletableFuture<AbstractEvent> abstractEventCompletableFuture = RequestEventConverter.processRequest(getUserEvent);
        return Mono.fromFuture(abstractEventCompletableFuture)
                .map(AbstractEvent::getApiResponse);
    }
}

