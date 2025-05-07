package com.example.service;

import com.example.event.AbstractEvent;
import reactor.core.publisher.Sinks;

public interface RequestStream {
    Sinks.EmitResult publishEvent(AbstractEvent event);
}
