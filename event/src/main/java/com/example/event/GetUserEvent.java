package com.example.event;

public class GetUserEvent extends AbstractEvent {
    private final String username;

    public GetUserEvent(String id, String host, String username) {
        super(id, host);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
