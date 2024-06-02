package Client;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RegisterLayout extends VBox {
    public RegisterLayout() {
        super(10);
        this.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Enter your nickname");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        Button confirmRegisterButton = new Button("Confirm Registration");
        Button backToLoginButton = new Button("Back to login");

        this.getChildren().addAll(usernameField, nicknameField, passwordField,
                confirmPasswordField, confirmRegisterButton, backToLoginButton);
    }

    public TextField getUsernameField() {
        return (TextField) this.getChildren().get(0);
    }

    public TextField getNicknameField() {
        return (TextField) this.getChildren().get(1);
    }

    public PasswordField getPasswordField() {
        return (PasswordField) this.getChildren().get(2);
    }

    public PasswordField getConfirmPasswordField() {
        return (PasswordField) this.getChildren().get(3);
    }

    public Button getRegisterButton() {
        return (Button) this.getChildren().get(4);
    }

    public Button getBackToLoginButton() {
        return (Button) this.getChildren().get(5);
    }

    public void clearFields() {
        this.getUsernameField().clear();
        this.getNicknameField().clear();
        this.getPasswordField().clear();
        this.getConfirmPasswordField().clear();
    }
}
