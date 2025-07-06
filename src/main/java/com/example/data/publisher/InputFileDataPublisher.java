package com.example.data.publisher;

import com.example.global.Message;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TransferQueue;

@Service
@Slf4j
@NoArgsConstructor
public class InputFileDataPublisher implements InterfaceDataPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(fixedRate = 200)
    @Override
    public void execute() {
        Message message = new Message(this, Instant.now().toEpochMilli(), "bla");
        applicationEventPublisher.publishEvent(message);
        log.info("Input file data publisher published an event");
    }
}
