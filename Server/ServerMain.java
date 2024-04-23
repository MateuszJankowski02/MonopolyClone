package Server;

import Utilities.Player;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerMain {
    static ArrayList<Player> players = new ArrayList<Player>();
    public static void main(String[] args) throws IOException {


        ServerSocket serverSocket = new ServerSocket(5001);
        System.out.println("Server Listening on port 5001...");

        while (true) {
            Socket connectionSocket = serverSocket.accept();
            new Thread(() -> {
                // Inside the thread created for each client connection
                try {
                    DataInputStream dataIn = new DataInputStream(connectionSocket.getInputStream());
                    DataOutputStream dataOut = new DataOutputStream(connectionSocket.getOutputStream());

                    String name = dataIn.readUTF();
                    players.add(new Player(name));
                    dataOut.writeUTF("Welcome, " + name + "! Your ID is " + players.get(players.size() - 1).getId());

                    connectionSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}