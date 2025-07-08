package com.example.global;

import lombok.Getter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedMessage implements Delayed {
    private final long scheduledTimeMillis;
    @Getter
    private final String message;

    public DelayedMessage(long baseTimestamp, long actualTimestamp, String message) {
        this.scheduledTimeMillis = System.currentTimeMillis() + (actualTimestamp - baseTimestamp);
        this.message = message;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delayMillis = scheduledTimeMillis - System.currentTimeMillis();
        return unit.convert(delayMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }
}

