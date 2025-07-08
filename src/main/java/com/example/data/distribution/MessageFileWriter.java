package com.example.data.distribution;

import com.example.global.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@AllArgsConstructor
public class MessageFileWriter implements ApplicationListener<Message> {

    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    private final Long startTime = Instant.now().toEpochMilli();
    private final String outputPath = "messages_since_" + startTime + ".csv";
    private final int batchSize = 10;


    @Scheduled(fixedRate = 500)
    public void flushToDisk() {
        if (queue.isEmpty()) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true))) {
            int count = 0;
            while (count < batchSize) {
                Message msg = queue.poll();
                if (msg == null) break;
                writer.write(msg.toString());
                count++;
            }
            writer.flush();
            log.info("Flushed {} messages to disk", count);
        } catch (IOException e) {
            log.error("Error writing to file", e);
        }
    }

    @Override
    public void onApplicationEvent(Message event) {
        queue.add(event);

        if (queue.size() > batchSize) {
            flushToDisk();
        }
    }
}
