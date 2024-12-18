package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

public class SimpleClient extends AbstractClient {
    private static SimpleClient client = null;
    SecondaryController sc;
    private SimpleClient(String host, int port) {
        super(host, port);
        sc = new SecondaryController();
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else if (message != null && message.startsWith("update")) {
                String[] splittedStr = message.split(" ");
                int row = Integer.parseInt(splittedStr[2]);
                int col = Integer.parseInt(splittedStr[3]);
                EventBus.getDefault().post(new Object[] { row, col, splittedStr[4] });
                message = splittedStr[4];
                sc.handleMessage(message);
            } else if (message != null && message.startsWith("done")) {
                EventBus.getDefault().post(msg + "   bom");
            }
            sc.handleMessage(message);
            System.out.println(message + "hahahahah");
        }
    }

    public static SimpleClient getClient() {
        if (client == null) {
            client = new SimpleClient("localhost", 3000);
        }
        return client;
    }

}
