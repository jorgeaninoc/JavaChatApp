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
 *  Concurrency Chat Application
 *  Author: Jorge Alberto Niño Cabal A01172309
 *  Date: 23/05/2020
 * */
package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

// Main class
public class Main {

    public static void main(String[] args) {
        // BufferedReader to receive the input of the user
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        // Ask the user if he wants to start a server or join one
        System.out.println("Would you like to join or start a server?");
        System.out.println("1. Start Solution Server");
        System.out.println("2. Start Problem Server");
        System.out.println("3. Join Solution Server");
        System.out.println("4. Join Problem Server");
        System.out.println("5. Exit");
        try {
            // Get user decision
            int option = Integer.parseInt(userInput.readLine());
            // If user wants to start a server
            if(option == 1){
                // Ask for the port to the user
                System.out.println("\n\n\nPlease insert the port: ");
                int port = Integer.parseInt(userInput.readLine());
                // Start server with port given
                Server s = new Server(port);
                s.start();
                try {
                    // Ask the user the name that will be given to ID the client
                    InetAddress address = InetAddress.getByName("localhost");
                    System.out.println("Please insert your name: ");
                    String name = userInput.readLine();
                    // Start the client
                    Client c = new Client(name, address, port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                // If he wants to create a non concurrent server
            } else if(option == 2) {
                // Ask for the port to the user
                System.out.println("\n\n\nPlease insert the port: ");
                int port = Integer.parseInt(userInput.readLine());
                // Start server with port given
                ProblemServer s = new ProblemServer(port);
                s.start();
                try {
                    // Ask the user the name that will be given to ID the client
                    InetAddress address = InetAddress.getByName("localhost");
                    System.out.println("Please insert your name: ");
                    String name = userInput.readLine();
                    // Start the client
                    ProblemClient c = new ProblemClient(name, address, port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                // If he wants to join a server
            } else if(option == 3) {
                try {
                    // Ask for the port
                    System.out.println("\n\n\nPlease insert the port: ");
                    int port = Integer.parseInt(userInput.readLine());
                    InetAddress address = InetAddress.getByName("localhost");
                    // Ask for the name to ID the client
                    System.out.println("Please insert your name: ");
                    String name = userInput.readLine();
                    // Start the client
                    Client c = new Client(name, address, port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }else if(option == 4) {
                try {
                    // Ask for the port
                    System.out.println("\n\n\nPlease insert the port: ");
                    int port = Integer.parseInt(userInput.readLine());
                    InetAddress address = InetAddress.getByName("localhost");
                    // Ask for the name to ID the client
                    System.out.println("Please insert your name: ");
                    String name = userInput.readLine();
                    // Start the client
                    ProblemClient c = new ProblemClient(name, address, port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }else if(option == 5) {
                System.out.println("Goodbye.");
            }
            else {
                System.out.println("Not a valid option.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
