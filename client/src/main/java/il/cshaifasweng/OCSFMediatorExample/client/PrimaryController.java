package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class PrimaryController {

	@FXML
	private AnchorPane anchor1;

	@FXML
	private Label searchLabel;

	@FXML
	private Button startGameBtn;


	@FXML
	void onClickBTN1(ActionEvent event) {
		try{
			SimpleClient.getClient().sendToServer("client ready");
			startGameBtn.setVisible(false);
			searchLabel.setText("Searching...");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onStartGameEvent(String event) {
		if (event.equals("startGame")) {
			try {
				System.out.println("primary startgame");
				switchToSecondary();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void switchToSecondary() throws IOException {
		App.setRoot("secondary");
	}

	@FXML
	void initialize() {
		EventBus.getDefault().register(this);

	}

}
