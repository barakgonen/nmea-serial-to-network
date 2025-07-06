package com.example.global;

public class Message {
    private long timeStamp;
    private String payload;

    public Message(long timeStamp, String payload) {
        this.timeStamp = timeStamp;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return timeStamp + "," + payload + '\n';
    }
}
