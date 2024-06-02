package Client;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CreateLobbyLayout extends VBox {
    public CreateLobbyLayout() {
        super(10);
        this.setAlignment(Pos.CENTER);

        TextField lobbyNameField = new TextField();
        lobbyNameField.setPromptText("Enter lobby name");
        TextField maxPlayersField = new TextField();
        maxPlayersField.setPromptText("Enter max players");
        Button createLobbyButton = new Button("Confirm");
        Button backToMainMenuButton = new Button("Back to main menu");

        this.getChildren().addAll(lobbyNameField, maxPlayersField, createLobbyButton, backToMainMenuButton);
    }

    public TextField getLobbyNameField() {
        return (TextField) this.getChildren().get(0);
    }

    public TextField getMaxPlayersField() {
        return (TextField) this.getChildren().get(1);
    }

    public Button getCreateLobbyButton() {
        return (Button) this.getChildren().get(2);
    }

    public Button getBackToMainMenuButton() {
        return (Button) this.getChildren().get(3);
    }

    public void clearFields() {
        this.getLobbyNameField().clear();
        this.getMaxPlayersField().clear();
    }
}
