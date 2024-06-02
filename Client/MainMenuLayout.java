package Client;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MainMenuLayout extends VBox {
    public MainMenuLayout() {
        super(10);
        this.setAlignment(Pos.CENTER);

        Button createLobbyButton = new Button("Create lobby");
        Button listLobbiesButton = new Button("List lobbies");
        Button logoutButton = new Button("Logout");
        Button exitButton = new Button("Exit");

        this.getChildren().addAll(createLobbyButton, listLobbiesButton, logoutButton, exitButton);
    }

    public Button getCreateLobbyButton() {
        return (Button) this.getChildren().get(0);
    }

    public Button getListLobbiesButton() {
        return (Button) this.getChildren().get(1);
    }

    public Button getLogoutButton() {
        return (Button) this.getChildren().get(2);
    }

    public Button getExitButton() {
        return (Button) this.getChildren().get(3);
    }
}
