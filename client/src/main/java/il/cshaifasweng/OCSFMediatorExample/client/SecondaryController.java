package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.application.Platform;


public class SecondaryController {
    @FXML
    private Button btn00,btn01,btn02,btn10,btn11,btn12,btn20,btn21,btn22;
    @FXML
    private Label textLabel;

    private SimpleClient client;
    int currentTurn ;



    public void initialize()
    {
        client = SimpleClient.getClient();
        client.setPlayerNumber((client.getPlayerNumber()));
        System.out.println("Current Player Number: " + client.getPlayerNumber() + " secondaryController");
        EventBus.getDefault().register(this);
        currentTurn = client.getCurrentTurn();
    }

    @FXML
     public void fillBoardOnClick (ActionEvent event) throws IOException {
        if (client.getPlayerNumber() == currentTurn) {
            Button currentButton = (Button) event.getSource();
            String btnID = currentButton.getId(); // e.g., "btn00"

            // Extract row and column from the button ID
            int row = btnID.charAt(3) - '0';
            int col = btnID.charAt(4) - '0';

            // Send move to the server
            client.sendMoveToServer(row, col);
        }
        else System.out.println("its not your turn");
        }


    @Subscribe
    public void Win (String winner){
        Platform.runLater(() -> {
            if (winner.startsWith("Winner")) {
                disableAllButtons();
                if (winner.contains("X")) {
                    System.out.println("Player1 Wins");
                    if (client.getPlayerNumber() == 1) {
                        textLabel.setText("You Win!");
                    } else {
                        textLabel.setText("You Lose!");
                    }
                } else {
                    System.out.println("Player2 Wins!");
                    if (client.getPlayerNumber() == 1) {
                        textLabel.setText("You Lose!");
                    } else {
                        textLabel.setText("You Win!");
                    }
                }
            }

            if (winner.startsWith("Draw")) {
                disableAllButtons();
                textLabel.setText("It's a Draw!");
            }
        });
    }

    @Subscribe
    public void onMoveEvent (MoveEvent move) {
        System.out.println("MoveEvent: Row=" + move.getRow() + ", Col=" + move.getCol() + ", Mark=" + move.getMark());
        Button button = getButtonByPosition(move.getRow(), move.getCol());
        if (button != null) {
            Platform.runLater(() -> {
                button.setText(String.valueOf(move.getMark())); // Update the text
                button.setDisable(true); // Disable the button after updating text
                System.out.println("Button updated and disabled: " + button.getId());
            });

            System.out.println("Button updated: " + button.getId());
        } else {
            System.out.println("Button not found for: Row=" + move.getRow() + ", Col=" + move.getCol());
        }
    }

    @Subscribe
    public void onTurnUpdate(String turnInfo) {
        // Update the current turn
        if (turnInfo.startsWith("TURN"))
        currentTurn = Integer.parseInt(turnInfo.split(" ")[1]);
       // updateTurnLabel(currentTurn);
    }
    private Button getButtonByPosition(int row, int col) {
        // Return the corresponding button based on row and column
        if (row == 0 && col == 0) return btn00;
        if (row == 0 && col == 1) return btn01;
        if (row == 0 && col == 2) return btn02;
        if (row == 1 && col == 0) return btn10;
        if (row == 1 && col == 1) return btn11;
        if (row == 1 && col == 2) return btn12;
        if (row == 2 && col == 0) return btn20;
        if (row == 2 && col == 1) return btn21;
        if (row == 2 && col == 2) return btn22;
        return null;
    }
    public void disableAllButtons() {
        btn00.setDisable(true);
        btn01.setDisable(true);
        btn02.setDisable(true);
        btn10.setDisable(true);
        btn11.setDisable(true);
        btn12.setDisable(true);
        btn20.setDisable(true);
        btn21.setDisable(true);
        btn22.setDisable(true);
    }
   }
