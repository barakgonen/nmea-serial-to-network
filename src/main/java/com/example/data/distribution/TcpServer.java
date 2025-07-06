package com.example.data.distribution;

import com.example.AppConfig;
import com.example.global.Message;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TransferQueue;

@Service
@Slf4j
public class TcpServer implements ApplicationListener<Message> {
    @Override
    public void onApplicationEvent(Message event) {
        log.info("TcpServer got an event: {}", event);
    }
}
