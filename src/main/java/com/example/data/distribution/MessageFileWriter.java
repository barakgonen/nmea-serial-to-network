package com.example.data.distribution;

import com.example.global.CsvMessage;
import com.example.global.Message;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@AllArgsConstructor
public class MessageFileWriter implements ApplicationListener<Message> {

    private static final String OUTPUT_DIR = "./output_events";
    private final File outputDir = new File(OUTPUT_DIR);;

    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Long startTime = Instant.now().toEpochMilli();
    private final String outputPath = OUTPUT_DIR + "/events_dump_" + startTime + ".csv";
    private final int batchSize = 5;


    @PostConstruct
    public void init() {

        if (!isOutputDirExists()) {
            if (!createOutputDir()) {
                log.error("Creation of output dir has failed!!!");
            } else {
                log.info("Created output dir successfully!");
            }
        }
    }

    @Scheduled(fixedRate = 500)
    public void flushToDisk() {
        if (queue.isEmpty()) return;

        File file = new File(outputPath);
        boolean isNewFile = !file.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {

            // Write header only once
            if (isNewFile) {
                writer.write("timeStamp,payload\n");
            }

            int count = 0;
            while (!queue.isEmpty() && count < batchSize) {
                Message msg = queue.poll();
                if (msg == null) continue;

                String line = formatCsvLine(msg);
                writer.write(line);
                writer.newLine();
                count++;
            }

            writer.flush();
            log.info("Flushed {} messages to disk", count);

        } catch (IOException e) {
            log.error("Failed to write CSV", e);
        }
    }

    private String formatCsvLine(Message msg) {
        String timestamp = String.valueOf(msg.getTimeStamp());
        String payload = clean(msg.getPayload());
        return timestamp + "," + payload;
    }

    private String clean(String s) {
        if (s == null) return "";
        return s.replace("\"", "")      // remove any quotes
                .replaceAll("[\\r\\n]+", "") // remove line breaks
                .trim();
    }

    private boolean isOutputDirExists() {
        if (!outputDir.exists()) {
            log.info("output dir is not exists, need to create it");
            return false;
        }

        log.info("Directory already exists");
        return true;
    }

    private boolean createOutputDir() {
        boolean created = outputDir.mkdirs(); // creates parent dirs too
        if (!created) {
            log.error("Failed to create directory");
            return false;
        }
        log.info("Directory created successfully");
        return true;
    }

    @Override
    public void onApplicationEvent(Message event) {
        queue.add(event);
        if (queue.size() > batchSize) {
            flushToDisk();
        }
    }
}
