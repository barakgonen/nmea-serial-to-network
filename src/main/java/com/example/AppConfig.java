package com.example;

import com.example.global.Message;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private ConnectionType connectionType;
    private TransferQueue<Message> bufferedMessages = new LinkedTransferQueue<>();
}
