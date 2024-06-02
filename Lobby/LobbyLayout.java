package Lobby;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class LobbyLayout extends VBox {
    public LobbyLayout() {
        super(10);
        this.setAlignment(Pos.CENTER);

        ListView<String> lobbyPlayersList = new ListView<>();
        Button leaveLobbyButton = new Button("Leave Lobby");
        Button startGameButton = new Button("Start Game");

        this.getChildren().addAll(lobbyPlayersList, leaveLobbyButton, startGameButton);
    }

    public ListView<String> getLobbyPlayersList() {
        return (ListView<String>) this.getChildren().get(0);
    }

    public Button getLeaveLobbyButton() {
        return (Button) this.getChildren().get(1);
    }

    public Button getStartGameButton() {
        return (Button) this.getChildren().get(2);
    }

    public void setLobbyPlayersList(ListView<String> lobbyPlayersList) {
        this.getChildren().set(0, lobbyPlayersList);
    }
}
