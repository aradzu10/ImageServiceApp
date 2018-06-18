package com.biu.dombelskizulti.imageserviceapp.Transfer;

import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private int port;
    private Socket socket;
    private HandleClientCommunication handler;

    public Client(int port, HandleClientCommunication handler) {
        this.port = port;
        this.handler = handler;
        this.socket = null;
    }

    public boolean Connect() {
        try {
            InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
            socket = new Socket(serverAddr, port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void Start() {
        new Thread() {
            @Override
            public void run() {
                try {
                    handler.handleCommunication(socket.getOutputStream());
                } catch (Exception e) { }
            }
        }.start();
    }
}