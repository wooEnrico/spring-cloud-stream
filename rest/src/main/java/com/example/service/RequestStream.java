package com.example.service;

import com.example.event.AbstractEvent;
import reactor.core.publisher.Sinks;

public interface RequestStream {
    default Sinks.EmitResult publishEvent(AbstractEvent event) {
        throw new UnsupportedOperationException("not implemented");
    }
}
