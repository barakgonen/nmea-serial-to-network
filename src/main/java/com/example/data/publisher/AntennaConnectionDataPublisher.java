package com.example.data.publisher;

import com.example.global.Message;
import com.fazecast.jSerialComm.SerialPort;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TransferQueue;

public class AntennaConnectionDataPublisher implements InterfaceDataPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute() {
        List<Message> messages = new ArrayList<>();
        SerialPort[] ports = SerialPort.getCommPorts();

        if (ports.length == 0) {
            System.out.println("No serial ports found.");
            return;
        }

        // List all available ports
        System.out.println("Available serial ports:");
        for (int i = 0; i < ports.length; i++) {
            System.out.println("[" + i + "] " + ports[i].getSystemPortName() + " - " + ports[i].getDescriptivePortName());
        }

        // User selects a port
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of the port to open: ");
        int index = scanner.nextInt();

        if (index < 0 || index >= ports.length) {
            System.out.println("Invalid selection.");
            return;
        }

        SerialPort port = ports[index];

        // Configure the port for NMEA 0183-HS
        port.setBaudRate(38400);
        port.setNumDataBits(8);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setParity(SerialPort.NO_PARITY);

        if (!port.openPort()) {
            System.out.println("Failed to open port.");
            return;
        }

        System.out.println("Port opened. Reading NMEA data...\n");

        try (InputStream in = port.getInputStream()) {
            byte[] buffer = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int numRead = in.read(buffer);
                    String received = new String(buffer, 0, numRead);
                    applicationEventPublisher.publishEvent(new Message(this, Instant.now().toEpochMilli(), received));
                }

                Thread.sleep(100); // avoid CPU spinning
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            port.closePort();
        }
    }
}
