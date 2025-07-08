package com.example.data.publisher;

import com.example.global.Message;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import java.io.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class AntennaConnectionDataPublisher implements InterfaceDataPublisher {

    // Socket read configuration
    @Value("${socket.buffer.size}")
    private int BUFFER_SIZE;
    @Value("${socket.poll.delay.ms}")
    private int POLL_DELAY_MS;

    // Serial port configuration
    @Value("${serial.port.baud.rate}")
    private int BAUD_RATE;
    @Value("${serial.port.data.bits.size}")
    private int DATA_BITS_SIZE;
    @Value("${serial.port.num.of.stop.bits}")
    private int NUM_OF_STOP_BITS;
    @Value("${serial.port.set.parity}")
    private int SET_PARITY;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute() {

        Optional<SerialPort> serialPortOptional = getSerialPort();

        if (serialPortOptional.isEmpty()) {
            log.error("Failed setting up a serial port, rejecting");
            return;
        }

        var port = serialPortOptional.get();
        if (!port.openPort()) {
            log.error("Failed to open port.");
            return;
        }

        log.info("Port opened. Reading NMEA data...");

        try (InputStream in = port.getInputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];

            while (!Thread.currentThread().isInterrupted()) {
                readAvailableBytes(in, buffer);
                Thread.sleep(POLL_DELAY_MS);
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt(); // reset interrupt flag
            log.error("Serial port reading interrupted or failed", e);
        } finally {
            port.closePort();
        }
    }

    private void readAvailableBytes(InputStream in, byte[] buffer) throws IOException {
        while (in.available() > 0) {
            int numRead = in.read(buffer);
            if (numRead > 0) {
                String received = new String(buffer, 0, numRead);
                List<String> splitMessages = Arrays.stream(received.split("\n")).toList();
                log.info("1 message split to: {} messages", splitMessages.size());
                Arrays
                        .stream(received.split("\n"))
                        .forEach(s ->
                                applicationEventPublisher.publishEvent(new Message(this, Instant.now().toEpochMilli(), s)));
            }
        }
    }

    private Optional<SerialPort> getSerialPort() {
        SerialPort[] ports = SerialPort.getCommPorts();

        if (ports.length == 0) {
            log.error("No serial ports found.");
            return Optional.empty();
        }

        // List all available ports
        // sout because it's an interaction with the user
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
            return Optional.empty();
        }

        SerialPort port = ports[index];
        // Configure the port for NMEA 0183-HS
        port.setBaudRate(BAUD_RATE);
        port.setNumDataBits(DATA_BITS_SIZE);
        port.setNumStopBits(NUM_OF_STOP_BITS);
        port.setParity(SET_PARITY);

        return Optional.of(port);
    }
}
