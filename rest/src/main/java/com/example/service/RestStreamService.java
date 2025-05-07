package com.example.service;

import com.example.converter.RequestEventConverter;
import com.example.event.AbstractEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@ConditionalOnProperty(name = "kafka.configuration.enabled", matchIfMissing = true, havingValue = "false")
public class RestStreamService implements RequestStream {
    private final static Logger log = org.slf4j.LoggerFactory.getLogger(RestStreamService.class);

    private final ObjectMapper objectMapper;
    private final Sinks.Many<AbstractEvent> eventSink;

    @Autowired
    public RestStreamService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.eventSink = Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Consumer<Message<String>> messageConsumer() {
        return message -> {
            try {
                AbstractEvent event = objectMapper.readValue(message.getPayload(), AbstractEvent.class);
                this.processEvent(event);
            } catch (Exception e) {
                log.error("Error deserializing event: {}", message, e);
            }
        };
    }

    @Bean
    public Supplier<Flux<Message<String>>> messageProducer() {
        return () -> eventSink.asFlux().mapNotNull(event -> {
            try {
                String eventJson = objectMapper.writeValueAsString(event);
                return MessageBuilder.withPayload(eventJson)
                        .setHeader("@type", event.getClass().getName())
                        .build();
            } catch (Exception e) {
                System.err.println("Error serializing event: " + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public Sinks.EmitResult publishEvent(AbstractEvent event) {
        return eventSink.tryEmitNext(event);
    }

    private void processEvent(AbstractEvent event) {
        log.info("Processing event: {}", event);
        RequestEventConverter.processResponse(event);
    }
}
