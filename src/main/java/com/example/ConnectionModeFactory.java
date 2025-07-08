package com.example;

import com.example.config.AppConfig;
import com.example.config.InputFileConfig;
import com.example.data.publisher.AntennaConnectionDataPublisher;
import com.example.data.publisher.InputFileDataPublisher;
import com.example.data.publisher.InterfaceDataPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionModeFactory {
    @Bean
    public static InterfaceDataPublisher getConnectionHandler(AppConfig appConfig, InputFileConfig inputFileConfig) {
        return switch (appConfig.getConnectionType()) {
            case ANTENNA -> new AntennaConnectionDataPublisher();
            case INPUT_FILE -> new InputFileDataPublisher(inputFileConfig);
        };
    }
}
