/*
 This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *  Final Proyect
 *  Concurrenct Server
 *  Author: Jorge Alberto Niño Cabal A01172309
 *  Date: 23/05/2020
 * */

package com.company;

import java.io.IOException;
import java.net.*;
import java.util.LinkedHashSet;

// Server class which receives all messages from the clients and broadcasts them to all the clients connected
public class Server extends Thread implements Runnable{

    // Declare DatagramSocket for the server to receive and send messages
    private final DatagramSocket socket;

    // Declare list of ports of the clients connected to broadcast the messages
    private final LinkedHashSet<Integer> listOfPorts;

    // Server constructor receives the port as a parameter
    public Server(Integer port) throws SocketException {
        this.socket = new DatagramSocket(port);
        listOfPorts = new LinkedHashSet<>();
    }

    // Function used to send the messages received by the server to all the clients
    public synchronized void broadCastMessage(byte[] buf){
        listOfPorts.forEach(port -> {
            InetAddress address = null;
            try {
                address = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, port);
            sendMessage(packet);
        });
    }

    public synchronized void sendMessage(DatagramPacket packet){
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function used to add clients to the list of ports
    public void addToListOfPorts(int port){
        listOfPorts.add(port);
        // For each client that joins the chat initiate one
        // Thread that will listen to the port's messages.
        PortListener pl = new PortListener(port);
        pl.start();
    }

    // Function to to check if client is already the list of ports
    public boolean isInListOfPorts(int port){
        return listOfPorts.contains(port);
    }

    // Function to receive the messages from the socket
    public synchronized void receiveMessage(DatagramPacket packet){
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
            synchronized (this){
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
                    // Broadcast message to all the clients
                    broadCastMessage(buf);
                } else {
                    broadCastMessage(buf);
                }
            }
        }
        // Close socket
        socket.close();
    }

    public class PortListener extends Thread implements Runnable {

        private int port;
        private InetAddress address;

        // PortListener constructor which receives the port it will listen
        public PortListener(int port){
            this.port = port;
            try {
                this.address = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            // Declare variable to check if thread should continue running
            boolean running = true;
            while (running) {
                // Receive the message
                synchronized (this){
                    byte[] buf = new byte[256];
                    // Receive all the messages send through the specified port
                    DatagramPacket packet
                            = new DatagramPacket(buf, buf.length, this.address, this.port);
                    receiveMessage(packet);
                    // Parse the message to string
                    String received
                            = new String(packet.getData(), 0, packet.getLength());
                    if (received.equals("end")) {
                        running = false;
                        continue;
                    }
                    broadCastMessage(buf);
                }
            }
            // Close socket
            socket.close();
        }


    }

}


