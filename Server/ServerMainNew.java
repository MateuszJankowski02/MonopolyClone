package Server;

import Lobby.Lobby;
import User.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerMainNew {
    final static int PORT = 8080;
    static public HashMap<Integer, ClientHandler> clients = new HashMap<>();
    static public Lobby.Lobbies lobbies = new Lobby.Lobbies();
    static public User.Users users = new User.Users();
    static int i = 0;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);
            Socket socket;

            System.out.println("Server started on port " + PORT + "...");

            while (true) {
                socket = server.accept();
                System.out.println("New client request received: " + socket);

                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());

                System.out.println("Creating a new handler for this client...");

                ClientHandler client = new ClientHandler(socket, i, dataIn, dataOut);

                Thread thread = new Thread(client);

                System.out.println("Adding this client to active client list");

                clients.put(i ,client);

                thread.start();

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void notifyClient(int clientID, Lobby lobby){
        //display users
        lobby.getUsers().forEach(user -> System.out.println("TEST2 " + user.getNickname()));

        //notify client
        clients.get(clientID).notifyListenersRefreshLobbyUsers(lobby);
    }
}
