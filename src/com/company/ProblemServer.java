/*
 *  Final Proyect
 *  Concurrency Chat Application
 *  Author: Jorge Alberto Niño Cabal A01172309
 *  Date: 23/05/2020
 * */

package com.company;

import java.io.IOException;
import java.net.*;
import java.util.LinkedHashSet;

// Server class which receives all messages from the clients and broadcasts them to all the clients connected
public class ProblemServer extends Thread implements Runnable{

    // Declare DatagramSocket for the server to receive and send messages
    private final DatagramSocket socket;

    // Declare list of ports of the clients connected to broadcast the messages
    private final LinkedHashSet<Integer> listOfPorts;

    // Server constructor receives the port as a parameter
    public ProblemServer(Integer port) throws SocketException {
        this.socket = new DatagramSocket(port);
        listOfPorts = new LinkedHashSet<>();
    }

    // Function used to send the messages received by the server to all the clients
    public void broadCastMessage(byte[] buf){
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

    // Function used to add clients to the list of ports
    public void addToListOfPorts(int port){
        listOfPorts.add(port);
    }

    // Function to to check if client is already the list of ports
    public boolean isInListOfPorts(int port){
        return listOfPorts.contains(port);
    }

    // Function to receive the messages from the socket
    public void receiveMessage(DatagramPacket packet){
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Main function of the server thread
    @Override
    public void run() {
        // Declare variable to check if thread should continue running
        boolean running = true;
        while (running) {
            // Receive the message
            byte[] buf = new byte[256];
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            receiveMessage(packet);
            // Get the port from the message
            int port = packet.getPort();
            // Parse the message to string
            String received
                    = new String(packet.getData(), 0, packet.getLength());
            if (received.equals("end")) {
                running = false;
                continue;
            }
            // If the port isn't in the list of ports add it
            if(!isInListOfPorts(port)){
                this.addToListOfPorts(port);
            }
            // Broadcast message to all the clients
            broadCastMessage(buf);
        }
        // Close socket
        socket.close();
    }

}
