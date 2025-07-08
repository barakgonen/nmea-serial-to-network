package com.example.data.distribution;

import com.example.data.fetcher.InterfaceEventsFetcher;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataTransmitter {
    @Autowired
    InterfaceEventsFetcher interfaceDataPublisher;

    @PostConstruct
    public void start() {
        interfaceDataPublisher.execute();
    }
}
