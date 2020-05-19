package com.company;

import java.io.IOException;
import java.net.*;
import java.util.LinkedHashSet;

public class Server extends Thread implements Runnable {

    private DatagramSocket socket;
    private boolean running;
    private int port;
    private LinkedHashSet<Integer> listOfPorts;
    private byte[] buf = new byte[256];

    public Server(Integer port) throws SocketException {
        socket = new DatagramSocket(port);
        this.port = port;
        listOfPorts = new LinkedHashSet<Integer>();
    }

    public synchronized void receiveMessage(DatagramPacket packet){
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(byte[] buf){
        listOfPorts.forEach(port -> {
            InetAddress address = null;
            try {
                address = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, port);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void run() {
        running = true;
        while (running) {
            buf = new byte[256];
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            this.receiveMessage(packet);
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            listOfPorts.add(port);
            String received
                    = new String(packet.getData(), 0, packet.getLength());
            if (received.equals("end")) {
                running = false;
                continue;
            } else {
                buf = new byte[256];
                buf = received.getBytes();
                sendMessage(buf);
            }
        }
        socket.close();
    }

}
