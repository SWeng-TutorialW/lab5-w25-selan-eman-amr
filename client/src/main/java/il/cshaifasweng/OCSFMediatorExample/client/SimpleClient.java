package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

public class SimpleClient extends AbstractClient {
    private static SimpleClient client = null;
    private SimpleClient(String host, int port) {
        super(host, port);
        new SecondaryController();
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof Warning) {
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
        } else {
            String message = msg.toString();
            if (message != null && message.startsWith("Start game")) {
                javafx.application.Platform.runLater(() -> {
                    try {
                        PrimaryController.setWaitingForPlayer(false);
                        PrimaryController.switchToSecondary();       // Switch to secondary screen
                        EventBus.getDefault().post("StartGame");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else if (message != null && message.startsWith("update") || message.startsWith("X") || message.startsWith("O")) {
                String[] splittedStr = message.split(" ");
                int row = Integer.parseInt(splittedStr[2]);
                int col = Integer.parseInt(splittedStr[3]);
                EventBus.getDefault().post(new Object[] { row, col, splittedStr[4] });
            } else if (message != null && message.startsWith("done")) {
                String[] splittedStr = message.split(" ");
                int row = Integer.parseInt(splittedStr[2]);
                int col = Integer.parseInt(splittedStr[3]);
//                SecondaryController.done("PLAYER "+ splittedStr[1]+ " WINNER!");
            }
            System.out.println(message);
        }
    }

    public static SimpleClient getClient() {
        if (client == null) {
            client = new SimpleClient("localhost", 3000);
        }
        return client;
    }

}
