package Server;

import Login.Login;
import Login.Register;
import Login.User;
import Lobby.Lobby;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMain {
    public static User.Users users = new User.Users();
    static HashMap<String, Lobby> lobbies = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Register register = new Register();
        Login login = new Login();

        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server started on port 8080");
        while (true) {
            Socket socket = server.accept();
            new Thread(() -> {
                try {
                    DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                    String option;

                    while (true){
                        option = dataIn.readUTF(); // read option from client 1
                        switch(option){
                            case "login":
                                String username = dataIn.readUTF(); // read username from client 2
                                String password = dataIn.readUTF(); // read password from client 3
                                User currentUser = login.loginUser(username, password);
                                if(currentUser != null){
                                    dataOut.writeUTF("login successful\n"); // write response to client 4
                                    dataOut.writeBoolean(true); // write boolean to client 5
                                }else{
                                    dataOut.writeUTF("login failed\n"); // write response to client 4
                                    dataOut.writeBoolean(false); // write boolean to client 5
                                }
                                break;
                            case "register":
                                String nicknameInput = dataIn.readUTF(); // read nickname from client 2
                                String loginInput = dataIn.readUTF(); // read login from client 3
                                String passwordInput = dataIn.readUTF(); // read password from client 4
                                if (register.checkLogin(loginInput)){
                                    dataOut.writeUTF("Login already exists\n"); // write response to client 5
                                }else {
                                    register.registerUser(nicknameInput, loginInput, passwordInput);
                                    dataOut.writeUTF("Registration successful\n"); // write response to client 5
                                    users.refresh();
                                }
                                break;
                            case "createLobby":
                                String lobbyNameCreate = dataIn.readUTF();
                                int maxPlayers = dataIn.readInt();
                                if(maxPlayers < 2 || maxPlayers > 4){
                                    dataOut.writeUTF("invalid number of players");
                                    break;
                                }
                                User owner = users.getUserById(dataIn.readInt());
                                Lobby lobbyCreate = new Lobby(owner, lobbyNameCreate, maxPlayers);
                                lobbies.put(lobbyNameCreate, lobbyCreate);
                                break;
                            case "joinLobby":
                                String lobbyNameJoin = dataIn.readUTF();
                                User player = users.getUserById(dataIn.readInt());
                                Lobby lobbyJoin = lobbies.get(lobbyNameJoin);
                                if (lobbyJoin == null){
                                    dataOut.writeUTF("Lobby does not exist");
                                    dataOut.writeBoolean(false);
                                    break;
                                }
                                if(lobbyJoin.getPlayers().size() < lobbyJoin.getMaxPlayers()){
                                    lobbyJoin.addPlayer(player);
                                    dataOut.writeUTF("Joined lobby");
                                    dataOut.writeBoolean(true);
                                    break;
                                }
                                if (lobbyJoin.isFull()){
                                    dataOut.writeUTF("Lobby is full");
                                    dataOut.writeBoolean(false);
                                    break;
                                }
                                dataOut.writeUTF("Unknown error occurred");
                                dataOut.writeBoolean(false);
                                break;
                            case "listLobbies":
                                dataOut.writeInt(lobbies.size());
                                for(String lobbyName : lobbies.keySet()){
                                    Lobby lobby = lobbies.get(lobbyName);
                                    dataOut.writeUTF(lobby.getLobbyName());
                                    dataOut.writeUTF(String.valueOf(lobby.getPlayers().size()));
                                    dataOut.writeUTF(String.valueOf(lobby.getMaxPlayers()));
                                    dataOut.writeUTF(lobby.getOwner().getNickname());
                                }
                                break;
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    }
            }).start();

        }
    }
}