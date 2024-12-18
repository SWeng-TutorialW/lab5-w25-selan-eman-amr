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
    private static String[][] gameBoard = new String[3][3];
    private static Button ClickedBtn;
    public SecondaryController() {
        EventBus.getDefault().register(this);  // Register to EventBus
    }
    @FXML
    private static Label gameState;

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void handleClickButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonId = button.getId();
        ClickedBtn = button;
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
        System.out.println("Received Message: " + msg);
    }
}