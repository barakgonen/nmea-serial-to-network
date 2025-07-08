package com.example.data.distribution;

import com.example.global.Message;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class TcpClientHandler implements ApplicationListener<Message> {
    private final AtomicReference<Socket> clientSocket = new AtomicReference<>();

    public void setClientSocket(Socket socket) {
        this.clientSocket.set(socket);
    }

    @Override
    public void onApplicationEvent(Message event) {
        try {
            Socket socket = clientSocket.get();
            if (socket != null && socket.isConnected()) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(event.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

