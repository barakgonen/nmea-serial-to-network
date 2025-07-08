package com.example.data.distribution;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class TcpServer implements Runnable {
    private final TcpClientHandler clientHandler;

    public TcpServer(TcpClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        new Thread(this).start(); // start TCP server in background
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("TCP Server started on port 12345");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                clientHandler.setClientSocket(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
