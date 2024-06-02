package Server;

import Lobby.Lobby;
import User.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerMainNew {
    final static int PORT = 8080;
    static public Vector<ClientHandler> clients = new Vector<>();
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

                ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

                System.out.println("Creating a new handler for this client...");

                ClientHandler client = new ClientHandler(socket, "client " + i, objectIn, objectOut);

                Thread thread = new Thread(client);

                System.out.println("Adding this client to active client list");

                clients.add(client);

                thread.start();

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
