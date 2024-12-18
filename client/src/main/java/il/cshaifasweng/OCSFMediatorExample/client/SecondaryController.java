package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SecondaryController {
    private String[][] gameBoard = new String[3][3];
    public SecondaryController() {
        EventBus.getDefault().register(this);  // Register to EventBus
    }
    @FXML
    private Label gameState;
    @FXML
    private Button btn00,btn01,btn02,btn10,btn11,btn12,btn20,btn21,btn22;
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }


    @FXML
    private void handleClickButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonId = button.getId();
        int row = Character.getNumericValue(buttonId.charAt(3));
        int col = Character.getNumericValue(buttonId.charAt(4));
        try {
            SimpleClient.getClient().sendToServer("New Move " + row + " " + col);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void handleMessage(Object msg) {
        Platform.runLater(() -> {
            if (msg instanceof String) {
                String message = msg.toString();
                System.out.println("I'm here ");
                if (message.equals("X") || message.equals("O")) {
                    gameState.setText(message + "'s Turn");
                } else if (message.startsWith("Player") || message.contains("done")) {
                    gameState.setText(message);
                }
            } else if (msg instanceof Object[]) { // Board update
                Object[] update = (Object[]) msg;
                int row = (int) update[0];
                int col = (int) update[1];
                String operation = (String) update[2];
                setGame(row, col, operation);
            }
        });
    }


    private void setGame(int row, int col, String operation) {
        gameBoard[row][col] = operation;
        if (row == 0 && col == 0) btn00.setText(operation);
        else if (row == 0 && col == 1) btn01.setText(operation);
        else if (row == 0 && col == 2) btn02.setText(operation);
        else if (row == 1 && col == 0) btn10.setText(operation);
        else if (row == 1 && col == 1) btn11.setText(operation);
        else if (row == 1 && col == 2) btn12.setText(operation);
        else if (row == 2 && col == 0) btn20.setText(operation);
        else if (row == 2 && col == 1) btn21.setText(operation);
        else if (row == 2 && col == 2) btn22.setText(operation);
    }
}