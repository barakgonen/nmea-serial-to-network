package com.example.global;

import org.springframework.context.ApplicationEvent;

public class Message extends ApplicationEvent {
    private long timeStamp;
    private String payload;

    public Message(Object source, long timeStamp, String payload) {
        super(source);
        this.timeStamp = timeStamp;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return timeStamp + "," + payload + '\n';
    }
}
