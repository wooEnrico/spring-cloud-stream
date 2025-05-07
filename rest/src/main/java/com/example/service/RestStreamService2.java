package com.example.service;

import com.example.converter.RequestEventConverter;
import com.example.event.AbstractEvent;
import com.example.utils.NetUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wooenrico.kafka.sender.DefaultReactorKafkaSender;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.SenderRecord;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "kafka.configuration.enabled", matchIfMissing = false, havingValue = "true")
public class RestStreamService2 implements RequestStream {
    private final static Logger log = org.slf4j.LoggerFactory.getLogger(RestStreamService2.class);

    private final ObjectMapper objectMapper;
    private final DefaultReactorKafkaSender kafkaProducer;
    private final String requestMessageTopic;

    @Autowired
    public RestStreamService2(ObjectMapper objectMapper, DefaultReactorKafkaSender kafkaProducer,
            @Value("${http.request.message.topic}") String requestMessageTopic) {
        this.objectMapper = objectMapper;
        this.kafkaProducer = kafkaProducer;
        this.requestMessageTopic = requestMessageTopic;
    }

    @Bean
    public Function<List<String>, List<String>> messageResponseTopic() {
        return list -> list.stream().map(s -> s + "-" + NetUtil.getHostId()).collect(Collectors.toList());
    }

    @Bean
    public Consumer<ConsumerRecord<String, String>> messageResponseConsumer() {
        return message -> {
            try {
                AbstractEvent event = objectMapper.readValue(message.value(), AbstractEvent.class);
                this.processEvent(event);
            } catch (Exception e) {
                log.error("Error deserializing event: {}", message, e);
            }
        };
    }

    private void processEvent(AbstractEvent event) {
        log.info("Processing event: {}", event);
        RequestEventConverter.processResponse(event);
    }

    @Override
    public Sinks.EmitResult publishEvent(AbstractEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(requestMessageTopic, eventJson);
            producerRecord.headers().add("@type", event.getClass().getName().getBytes(StandardCharsets.UTF_8));
            final SenderRecord<String, String, ProducerRecord<String, String>> senderRecord = SenderRecord.create(producerRecord, producerRecord);
            return kafkaProducer.emitToSinks(senderRecord);
        } catch (Exception e) {
            log.error("Error serializing event: {}", event, e);
        }
        return Sinks.EmitResult.FAIL_CANCELLED;
    }
}
