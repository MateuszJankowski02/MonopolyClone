package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

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

    public void setVisiblePlayers(int amountOfPlayers){
        switch (amountOfPlayers){
            case 2:
                playerOneLabel.setVisible(true);
                playerOnePiece.setVisible(true);            
                playerOnePieceExample.setVisible(true);

                playerTwoLabel.setVisible(true);
                playerTwoPiece.setVisible(true);
                playerTwoPieceExample.setVisible(true);


                break;
            case 3:
                break;
            case 4:
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

    public void setRollDiceButton(Button rollDiceButton) {
        this.rollDiceButton = rollDiceButton;
    }

    public void setEndTurnButton(Button endTurnButton) {
        this.endTurnButton = endTurnButton;
    }

    public void setCurrentPlayerLabel(Label currentPlayerLabel) {
        this.currentPlayerLabel = currentPlayerLabel;
    }

    public void setCurrentRollAmountLabel(Label currentRollAmountLabel) {
        this.currentRollAmountLabel = currentRollAmountLabel;
    }

    public void setPlayerOneLabel(Label playerOneLabel) {
        this.playerOneLabel = playerOneLabel;
    }

    public void setPlayerTwoLabel(Label playerTwoLabel) {
        this.playerTwoLabel = playerTwoLabel;
    }

    public void setPlayerThreeLabel(Label playerThreeLabel) {
        this.playerThreeLabel = playerThreeLabel;
    }

    public void setPlayerFourLabel(Label playerFourLabel) {
        this.playerFourLabel = playerFourLabel;
    }

    public void setPlayerOnePieceExample(Circle playerOnePieceExample) {
        this.playerOnePieceExample = playerOnePieceExample;
    }

    public void setPlayerTwoPieceExample(Circle playerTwoPieceExample) {
        this.playerTwoPieceExample = playerTwoPieceExample;
    }

    public void setPlayerThreePieceExample(Circle playerThreePieceExample) {
        this.playerThreePieceExample = playerThreePieceExample;
    }

    public void setPlayerFourPieceExample(Circle playerFourPieceExample) {
        this.playerFourPieceExample = playerFourPieceExample;
    }

    public void setPlayerOnePiece(Circle playerOnePiece) {
        this.playerOnePiece = playerOnePiece;
    }

    public void setPlayerTwoPiece(Circle playerTwoPiece) {
        this.playerTwoPiece = playerTwoPiece;
    }

    public void setPlayerThreePiece(Circle playerThreePiece) {
        this.playerThreePiece = playerThreePiece;
    }

    public void setPlayerFourPiece(Circle playerFourPiece) {
        this.playerFourPiece = playerFourPiece;
    }
}
