package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class GameController {

    @FXML
    private Button rollDiceButton;
    @FXML
    private Button endTurnButton;
    @FXML
    private Label currentPlayerLabel;
    @FXML
    private Label currentRollAmountLabel;
    @FXML
    private Label playerOneLabel;
    @FXML
    private Label playerTwoLabel;
    @FXML
    private Label playerThreeLabel;
    @FXML
    private Label playerFourLabel;
    @FXML
    private Circle playerOnePieceExample;
    @FXML
    private Circle playerTwoPieceExample;
    @FXML
    private Circle playerThreePieceExample;
    @FXML
    private Circle playerFourPieceExample;
    @FXML
    private Circle playerOnePiece;
    @FXML
    private Circle playerTwoPiece;
    @FXML
    private Circle playerThreePiece;
    @FXML
    private Circle playerFourPiece;

    @FXML
    private void initialize(){
    }

    @FXML
    private void rollDiceButtonOnAction(){
    }

    @FXML
    private void endTurnButtonOnAction(){
    }

    public void setPlayerLabels(ArrayList<String> userNicknames){
        int amountOfPlayers = userNicknames.size();
        switch (amountOfPlayers){
            case 2:
                playerOneLabel.setVisible(true);
                playerOneLabel.setText(userNicknames.get(0));
                playerOnePiece.setVisible(true);            
                playerOnePieceExample.setVisible(true);

                playerTwoLabel.setVisible(true);
                playerTwoLabel.setText(userNicknames.get(1));
                playerTwoPiece.setVisible(true);
                playerTwoPieceExample.setVisible(true);

                playerThreeLabel.setVisible(false);
                playerThreePiece.setVisible(false);
                playerThreePieceExample.setVisible(false);

                playerFourLabel.setVisible(false);
                playerFourPiece.setVisible(false);
                playerFourPieceExample.setVisible(false);

                break;
            case 3:
                playerOneLabel.setVisible(true);
                playerOneLabel.setText(userNicknames.get(0));
                playerOnePiece.setVisible(true);
                playerOnePieceExample.setVisible(true);

                playerTwoLabel.setVisible(true);
                playerTwoLabel.setText(userNicknames.get(1));
                playerTwoPiece.setVisible(true);
                playerTwoPieceExample.setVisible(true);

                playerThreeLabel.setVisible(true);
                playerThreeLabel.setText(userNicknames.get(2));
                playerThreePiece.setVisible(true);
                playerThreePieceExample.setVisible(true);

                playerFourLabel.setVisible(false);
                playerFourPiece.setVisible(false);
                playerFourPieceExample.setVisible(false);
                break;
            case 4:
                playerOneLabel.setVisible(true);
                playerOneLabel.setText(userNicknames.get(0));
                playerOnePiece.setVisible(true);
                playerOnePieceExample.setVisible(true);

                playerTwoLabel.setVisible(true);
                playerTwoLabel.setText(userNicknames.get(1));
                playerTwoPiece.setVisible(true);
                playerTwoPieceExample.setVisible(true);

                playerThreeLabel.setVisible(true);
                playerThreeLabel.setText(userNicknames.get(2));
                playerThreePiece.setVisible(true);
                playerThreePieceExample.setVisible(true);

                playerFourLabel.setVisible(true);
                playerFourLabel.setText(userNicknames.get(3));
                playerFourPiece.setVisible(true);
                playerFourPieceExample.setVisible(true);
                break;
            default:
                System.out.println("INVALID AMOUNT OF PLAYERS");
        }
    }
    public Button getRollDiceButton() {
        return rollDiceButton;
    }

    public Button getEndTurnButton() {
        return endTurnButton;
    }

    public Label getCurrentPlayerLabel(){
        return currentPlayerLabel;
    }

    public Label getCurrentRollAmountLabel() {
        return currentRollAmountLabel;
    }

    public Label getPlayerOneLabel() {
        return playerOneLabel;
    }

    public Label getPlayerTwoLabel() {
        return playerTwoLabel;
    }

    public Label getPlayerThreeLabel() {
        return playerThreeLabel;
    }

    public Label getPlayerFourLabel() {
        return playerFourLabel;
    }

    public Circle getPlayerOnePieceExample() {
        return playerOnePieceExample;
    }

    public Circle getPlayerTwoPieceExample() {
        return playerTwoPieceExample;
    }

    public Circle getPlayerThreePieceExample() {
        return playerThreePieceExample;
    }

    public Circle getPlayerFourPieceExample() {
        return playerFourPieceExample;
    }

    public Circle getPlayerOnePiece() {
        return playerOnePiece;
    }

    public Circle getPlayerTwoPiece() {
        return playerTwoPiece;
    }

    public Circle getPlayerThreePiece() {
        return playerThreePiece;
    }

    public Circle getPlayerFourPiece() {
        return playerFourPiece;
    }

    public void setCurrentPlayerLabelValue(String currentPlayer){
        currentPlayerLabel.setText(currentPlayer);
    }

    public void setCurrentRollAmountLabelValue(int currentRollAmount){
        currentRollAmountLabel.setText(String.valueOf(currentRollAmount));
    }
}
