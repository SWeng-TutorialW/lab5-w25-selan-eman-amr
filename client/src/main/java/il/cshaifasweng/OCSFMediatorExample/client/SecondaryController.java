package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SecondaryController {

    private char[][] board = new char[3][3];

    @FXML
     public void fillBoardOnClick (ActionEvent event) throws IOException {
      Button currentButton = (Button) event.getSource();
        String btnID = currentButton.getId();
        int i = btnID.charAt(3) - '0';
        int j = btnID.charAt(4) - '0';
       board[i][j] = 'X';
        currentButton.setText("X");
            }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}