package com.example.data.publisher;

import com.example.config.InputFileConfig;
import com.example.global.DelayedMessage;
import com.example.global.Message;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.DelayQueue;

@Slf4j
public class InputFileDataPublisher implements InterfaceDataPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    private String inputFilePath;
    private final DelayQueue<DelayedMessage> queue;

    public InputFileDataPublisher(InputFileConfig inputFileConfig) {
        this.inputFilePath = inputFileConfig.getPath();
        this.queue = new DelayQueue<>();
    }

    @Override
    public void execute() {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            long baseTimestamp = -1;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("timestamp")) continue;

                String[] parts = line.split(",", 2);
                if (parts.length != 2) continue;

                long timestamp = Long.parseLong(parts[0]);
                String message = parts[1];

                if (baseTimestamp == -1) {
                    baseTimestamp = timestamp;
                }

                queue.put(new DelayedMessage(baseTimestamp, timestamp, message));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (!queue.isEmpty()) {
            DelayedMessage msg = null;
            try {
                msg = queue.take();
                publishMessage(msg.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void publishMessage(String msg){
        applicationEventPublisher.publishEvent(new Message(this, Instant.now().toEpochMilli(), msg));
        log.info("Message is: {}, and it published to server", msg);
    }
}