package com.example.controller;

import com.example.event.GetUserEvent;
import com.example.response.ApiResponse;
import com.example.service.LocalRequestEventConverter;
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

@RestController
@RequestMapping("/test")
public class TestController {

    private final RequestStream streamService;
    private final LocalRequestEventConverter requestEventConverter;

    @Autowired
    public TestController(RequestStream streamService, LocalRequestEventConverter requestEventConverter) {
        this.streamService = streamService;
        this.requestEventConverter = requestEventConverter;
    }

    @GetMapping("/user/{username}")
    public Mono<ApiResponse> getUser(@PathVariable("username") String username) {
        GetUserEvent getUserEvent = new GetUserEvent(UUID.randomUUID().toString(), NetUtil.getHostId(), username);

        final Sinks.EmitResult emitResult = streamService.publishEvent(getUserEvent);
        if (emitResult.isFailure()) {
            return Mono.error(new RuntimeException("Failed to publish event"));
        }
        return Mono.fromFuture(requestEventConverter.request(getUserEvent));
    }
}

