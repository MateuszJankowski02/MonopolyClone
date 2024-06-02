package Lobby;

import javafx.scene.Scene;

public class LobbyScene extends Scene {
    public LobbyScene() {
        super(new LobbyLayout());
    }

    public LobbyLayout getLobbyLayout() {
        return (LobbyLayout) this.getRoot();
    }

    public void addUserToList(String nickname) {
        this.getLobbyLayout().getLobbyPlayersList().getItems().add(nickname);
    }

    public void removeUserFromList(String nickname) {
        this.getLobbyLayout().getLobbyPlayersList().getItems().remove(nickname);
    }
}
