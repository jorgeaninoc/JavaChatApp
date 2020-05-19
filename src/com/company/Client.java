package com.company;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;




public class Client {

    public String name;
    private InetAddress ip;
    private Integer port;
    private DatagramSocket socket;


    public Client(String name, InetAddress serverIP, Integer serverPort) {
        this.ip = serverIP;
        this.port = serverPort;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.socket = socket;
        this.socket.connect(this.ip, this.port);
        this.name = name;
        ClientWrite w = new ClientWrite(this.socket);
        ClientRead r = new ClientRead(this.socket);
        w.start();
        r.start();
        try{
            w.join();
            r.join();
        } catch (InterruptedException interrupt) {
            interrupt.printStackTrace();
        }

    }

    public synchronized String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }


    private class ClientWrite extends Thread implements Runnable {
        private byte[] buf;
        private DatagramSocket socket;

        public ClientWrite(DatagramSocket clientSocket){
            this.socket = clientSocket;
            this.buf = new byte[255];
            try {
                this.sendMessage(name + " joined the chat...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public synchronized Boolean sendMessage(String msg) throws IOException {

            buf = ("[" + getDate() + "] " + name + ": " + msg).getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, ip, port);
            this.socket.send(packet);
            buf = new byte[255];
            return true;
        }

        @Override
        public void run() {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            boolean running = true;
            String msg = "";
            while (running){
                try {
                    msg = userInput.readLine();
                    if(msg == "end"){
                        running = false;
                    } else {
                        this.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            this.socket.close();

        }
    }

    private class ClientRead extends Thread implements Runnable {
        private byte[] buf;
        private DatagramSocket socket;

        public ClientRead(DatagramSocket clientSocket){
            this.socket = clientSocket;
            this.buf = new byte[255];
        }

        public synchronized String receiveMessage() throws IOException {
            buf = new byte[255];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            this.socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            return received;
        }

        @Override
        public void run() {
            boolean running = true;

            String msg = "";
            while (running){
                try {
                    msg = receiveMessage();
                    if(msg == "end"){
                        running = false;
                    } else {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            this.socket.close();
        }
    }


}