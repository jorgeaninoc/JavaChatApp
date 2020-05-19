package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
	// write your code here
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Would you like to join or start a server?");
        System.out.println("1. Start Server");
        System.out.println("2. Join a Server");
        System.out.println("3. Exit");
        try {
            int option = Integer.parseInt(userInput.readLine());
            if(option == 1){
                System.out.println("\n\n\nPlease insert the port: ");
                int port = Integer.parseInt(userInput.readLine());
                Server s = new Server(port);
                s.start();
                try {
                    InetAddress address = InetAddress.getByName("localhost");
                    System.out.println("Please insert your name: ");
                    String name = userInput.readLine();
                    Client c = new Client(name, address, port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

            } else if(option == 2) {
                try {
                    System.out.println("\n\n\nPlease insert the port: ");
                    int port = Integer.parseInt(userInput.readLine());
                    InetAddress address = InetAddress.getByName("localhost");
                    System.out.println("Please insert your name: ");
                    String name = userInput.readLine();
                    Client c = new Client(name, address, port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Not a valid option.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
