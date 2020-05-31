/*
*  Final Proyect
*  Concurrency Chat Application
*  Author: Jorge Alberto Niño Cabal A01172309
*  Date: 23/05/2020
* */
package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;


// Client class represents the whole client with Read and Write threads
public class Client {

    // Needed variables to identify and run the client
    public String name;
    private InetAddress ip;
    private Integer port;
    private DatagramSocket socket;
    private boolean running;

    // Constructor of the Client class
    public Client(String name, InetAddress serverIP, Integer serverPort) {
        // IP and PORT needed to connect to the server
        this.ip = serverIP;
        this.port = serverPort;
        // Declaring the socket
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        // Storing the socket in the object
        this.socket = socket;
        // Connect to the IP and PORT of the server
        this.socket.connect(this.ip, this.port);
        // Identifying the Client
        this.name = name;
        // Allow threads to run
        this.running = true;
        // Initiating ClientWrite Thread and ClientRead Thread
        ClientWrite w = new ClientWrite(this.socket);
        ClientRead r = new ClientRead(this.socket, this.ip, this.port);
        // Starting both threads
        w.start();
        r.start();
        try{
            // Threads will wait until the other thread ends execution
            w.join();
            r.join();
        } catch (InterruptedException interrupt) {
            interrupt.printStackTrace();
        }

    }

    // Unsynchronized function to get and parse the current time
    public synchronized String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }


    // ClientWrite class to handle when the client sends message to the server
    private class ClientWrite extends Thread implements Runnable {

        // Needed variables for the ClientWrite class
        private byte[] buf;
        private DatagramSocket socket;
        private int counter;

        public synchronized int getCounter(){
            synchronized (this){
                return this.counter;
            }
        }

        public synchronized void addCounter(){
            synchronized (this){
                this.counter += 1;
            }
        }


        // ClientWrite constructor which receives the socket instantiated in the Client class
        public ClientWrite(DatagramSocket clientSocket){
            // Instantiate needed variables
            this.socket = clientSocket;
            this.counter = 1;
            this.buf = new byte[255];
            // Send successful connection message
            try {
                buf = "connection".getBytes();
                DatagramPacket packet
                        = new DatagramPacket(buf, buf.length, ip, port);
                this.socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Function used to send a message to the server
        public synchronized void sendMessage(String msg) throws IOException {
            buf = ("[" + getDate() + "] " + name + ": " + msg).getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, ip, port);
            this.socket.send(packet);
            buf = new byte[255];
        }


        // Main function of the thread
        @Override
        public void run() {
            // Function to receive the Input from the User (Unused in Problem explanation)
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            // Setting variable to check if client will continue running
            String msg = "";
            // While thread is running
            while (running){
                try {
                    synchronized (this){
                        msg = userInput.readLine();
                        // Message counter used in Problem explanation
                        // msg = "Message number " + getCounter();
                        if(msg.equals("end")){
                            running = false;
                        } else {
                            // Send message to the server
                            this.sendMessage(msg);
                            // addCounter();
                        }
                    }
                } catch (IOException e) {
                    running = false;
                }

            }
            // Close socket
            this.socket.close();

        }
    }

    // Function used to read messages received from the server
    private static class ClientRead extends Thread implements Runnable {

        // Needed variables to store the message received and the sock
        private byte[] buf;
        private DatagramSocket socket;
        private int port;
        private InetAddress ip;

        // Constructor of the ClientRead thread receives the client Socket as a parameter
        public ClientRead(DatagramSocket clientSocket, InetAddress ip, int port){
            this.ip = ip;
            this.port = port;
            this.socket = clientSocket;
            this.buf = new byte[255];
        }

        // Function that receives the messages from the server
        public synchronized String receiveMessage() throws IOException {
            buf = new byte[255];
            DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
            this.socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            return received;
        }

        // Main read function that receives messages from the server while the thread is running.
        @Override
        public void run() {
            // Setting variable to check if client will continue running
            String msg = "";
            boolean running = true;
            while (running){
                try {
                    synchronized (this){
                        msg = receiveMessage();
                        if(msg.equals("end")){
                            running = false;
                        } else {
                            System.out.println(msg);
                        }
                    }
                } catch (IOException e) {
                    running = false;
                }
            }
            // Close socket
            this.socket.close();
        }
    }


}