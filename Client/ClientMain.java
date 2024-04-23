package Client;

import Utilities.User;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientMain {
    private static Socket socket;
    private static DataInputStream dataIn;
    private static DataOutputStream dataOut;
    private static User user;


    public static void main(String[] args) throws IOException {
        // Create a new socket and attempt to connect to the server
        socket = new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1", 5001), 1000);
        System.out.println("Connection Successful!");

        // Initialize input and output streams for communication with the server
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter name:");
        dataOut.writeUTF(scanner.nextLine());
        //enter login


        // Receive and print the server's response
        String serverMessage = dataIn.readUTF();
        System.out.println("Result: " + serverMessage);
    }
}