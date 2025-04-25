package com.example.service;

import com.example.event.AbstractEvent;
import com.example.event.GetUserEvent;
import com.example.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Service
public class WorkerStreamService {
    private final static Logger log = org.slf4j.LoggerFactory.getLogger(WorkerStreamService.class);

    private final ObjectMapper objectMapper;
    private final StreamBridge streamBridge;

    @Autowired
    public WorkerStreamService(ObjectMapper objectMapper, StreamBridge streamBridge) {
        this.objectMapper = objectMapper;
        this.streamBridge = streamBridge;
    }

    @Bean
    public Consumer<Message<String>> messageConsumer() {
        return message -> {
            try {
                Object type = Optional.ofNullable(message).map(Message::getHeaders)
                        .map(headers -> headers.get("@type"))
                        .orElse(null);

                if (type instanceof String typeStr) {
                    Class<?> eventClass = Class.forName(typeStr);
                    if (AbstractEvent.class.isAssignableFrom(eventClass)) {
                        @SuppressWarnings("unchecked")
                        Class<? extends AbstractEvent> concreteEventClass = (Class<? extends AbstractEvent>) eventClass;
                        AbstractEvent event = objectMapper.readValue(message.getPayload(), concreteEventClass);
                        try {
                            // TODO : process the event
                            this.processEvent(event);
                            String eventJson = objectMapper.writeValueAsString(event);
                            Message<String> newMessage = MessageBuilder.withPayload(eventJson).build();
                            streamBridge.send("messages-response-" + event.getHost(), newMessage);
                        } catch (Exception e) {
                            log.error("Error serializing event: {}", event, e);
                        }

                    } else {
                        log.error("Event class {} is not a subclass of AbstractEvent", type);
                    }
                } else {
                    log.error("Invalid event type header: {}", type);
                }
            } catch (ClassNotFoundException e) {
                log.error("Event type not found: {}", message.getHeaders().get("@type"), e);
            } catch (Exception e) {
                log.error("Error deserializing event: {}", message, e);
            }
        };
    }

    private void processEvent(AbstractEvent event) {
        log.info("Processing event: {}", event);
        final ApiResponse apiResponse = event.getApiResponse();
        apiResponse.setError(null);
        apiResponse.setData("OK");
        apiResponse.setStatus(200);
        if (event instanceof GetUserEvent getUserEvent) {
            apiResponse.setMessage("Success get " + getUserEvent.getUsername());
        }
    }
}
