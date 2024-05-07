package Client;

import Login.Login;
import Login.Register;
import Login.User;
import Server.ServerMain;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientMain {
    private static Socket socket;
    private static DataInputStream dataIn;
    private static DataOutputStream dataOut;
    private static User loggedUser;
    private static boolean loggedIn = false;
    private static boolean connectedToLobby = false;

    public static void main(String[] args) throws IOException {
        socket = new Socket("localhost", 8080);
        System.out.println("Connected to server");
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Monopoly!");
        while(!loggedIn) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    dataOut.writeUTF("login"); // write option to server
                    System.out.print("Enter your username: ");
                    String username = scanner.nextLine();
                    dataOut.writeUTF(username); // write username to server
                    System.out.print("Enter your password: ");
                    String password = scanner.nextLine();
                    dataOut.writeUTF(password); // write password to server
                    System.out.print(dataIn.readUTF()); // read response from server
                    loggedIn = dataIn.readBoolean(); // read boolean from server
                    if (loggedIn) {
                        loggedUser = ServerMain.users.getUserByLogin(username);
                    }
                    break;
                case 2:
                    dataOut.writeUTF("register"); // write option to server 1
                    System.out.print("Enter your name: ");
                    String nickname = scanner.nextLine();
                    dataOut.writeUTF(nickname); // write nickname to server 2
                    System.out.print("Enter your username: ");
                    String login = scanner.nextLine();
                    dataOut.writeUTF(login); // write login to server 3
                    System.out.print("Enter your password: ");
                    String pass = scanner.nextLine();
                    dataOut.writeUTF(pass); // write password to server 4
                    System.out.print(dataIn.readUTF()); // read response from server 5
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }

        while (!connectedToLobby) {
            System.out.println("1. Create lobby");
            System.out.println("2. List lobbies");
            System.out.println("3. Join lobby");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    dataOut.writeUTF("createLobby");
                    System.out.print("Enter lobby name: ");
                    String lobbyName = scanner.nextLine();
                    dataOut.writeUTF(lobbyName);
                    System.out.print("Enter max players: ");
                    int maxPlayers = scanner.nextInt();
                    dataOut.writeInt(maxPlayers);
                    dataOut.writeInt(loggedUser.getId());
                    connectedToLobby = true;
                    // Wait for a response from the server
                    String serverResponseCreate = dataIn.readUTF();
                    System.out.println(serverResponseCreate);
                    break;
                case 2:
                    dataOut.writeUTF("listLobbies");
                    int lobbiesSize = dataIn.readInt();
                    for(int i = 0; i < lobbiesSize; i++) {
                        System.out.print("Name: " + dataIn.readUTF() + " | ");
                        System.out.print("Player count: " + dataIn.readUTF() + " | ");
                        System.out.print("Max players: " + dataIn.readUTF() + " | ");
                        System.out.print("Owner: " + dataIn.readUTF() + " | ");
                        System.out.print("\n");
                    }
                    break;
                case 3:
                    dataOut.writeUTF("joinLobby");
                    System.out.print("Enter lobby name: ");
                    String lobbyNameJoin = scanner.nextLine();
                    dataOut.writeUTF(lobbyNameJoin);
                    dataOut.writeInt(loggedUser.getId());
                    dataIn.readUTF();
                    if (dataIn.readBoolean()) {
                        connectedToLobby = true;
                    }
                    // Wait for a response from the server
                    String serverResponseJoin = dataIn.readUTF();
                    System.out.println(serverResponseJoin);
                    break;
                case 4:
                    System.exit(0);
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}