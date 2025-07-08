package com.example;

import com.example.config.AppConfig;
import com.example.config.InputFileConfig;
import com.example.data.fetcher.AntennaConnectionEventsFetcher;
import com.example.data.fetcher.InputFileEventsFetcher;
import com.example.data.fetcher.InterfaceEventsFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionModeFactory {
    @Bean
    public static InterfaceEventsFetcher getConnectionHandler(AppConfig appConfig, InputFileConfig inputFileConfig) {
        return switch (appConfig.getConnectionType()) {
            case ANTENNA -> new AntennaConnectionEventsFetcher();
            case INPUT_FILE -> new InputFileEventsFetcher(inputFileConfig);
        };
    }
}
