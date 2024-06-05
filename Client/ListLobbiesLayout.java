package Client;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;


public class ListLobbiesLayout extends VBox {

    private ListView<String> lobbiesList;

    public ListLobbiesLayout() {
        super(10);
        this.setAlignment(Pos.CENTER);

        lobbiesList = new ListView<>();
        Button refreshLobbiesButton = new Button("Refresh");
        Button backToMainMenuFromListButton = new Button("Back to main menu");

        this.getChildren().addAll(lobbiesList, refreshLobbiesButton, backToMainMenuFromListButton);
    }

    public ListView<String> getLobbiesList() {
        return (ListView<String>) this.getChildren().get(0);
    }

    public Button getRefreshLobbiesButton() {
        return (Button) this.getChildren().get(1);
    }

    public Button getBackToMainMenuFromListButton() {
        return (Button) this.getChildren().get(2);
    }

    public void addLobby(String lobbyName) {
        lobbiesList.getItems().add(lobbyName);
    }

    public void clearLobbies() {
        lobbiesList.getItems().clear();
    }

}
