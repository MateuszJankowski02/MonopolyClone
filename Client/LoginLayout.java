package Client;

import Login.User;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.awt.*;

public class LoginLayout extends VBox {
    public LoginLayout() {
        super(10);
        this.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        this.getChildren().addAll(usernameField, passwordField, loginButton, registerButton);
    }

    public TextField getUsernameField() {
        return (TextField) this.getChildren().get(0);
    }

    public PasswordField getPasswordField() {
        return (PasswordField) this.getChildren().get(1);
    }

    public Button getLoginButton() {
        return (Button) this.getChildren().get(2);
    }

    public Button getRegisterButton() {
        return (Button) this.getChildren().get(3);
    }

    public void clearFields() {
        this.getUsernameField().clear();
        this.getPasswordField().clear();
    }
}
