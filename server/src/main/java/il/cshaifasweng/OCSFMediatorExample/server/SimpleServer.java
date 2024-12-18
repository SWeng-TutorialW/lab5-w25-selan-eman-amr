package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
    private String[][] gameBoard = new String[3][3];
    private boolean isOTurn = true;

    public SimpleServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String msgString = msg.toString();
        if (msgString.startsWith("#warning")) {
            Warning warning = new Warning("Warning from server!");
            try {
                client.sendToClient(warning);
                System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msgString.startsWith("add client")) {
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);
            try {
                client.sendToClient("client added successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (msgString.startsWith("remove client")) {
            if (!SubscribersList.isEmpty()) {
                for (SubscribedClient subscribedClient : SubscribersList) {
                    if (subscribedClient.getClient().equals(client)) {
                        SubscribersList.remove(subscribedClient);
                        break;
                    }
                }
            }
        } else if (msgString.startsWith("I'm here")) {
            if (SubscribersList.size() == 1) {
                sendToAllClients("Start game");
            }
        } else if (msgString.startsWith("New Move ")) {
            String[] splittedStr = msgString.split(" ");
            int row = Integer.parseInt(splittedStr[2]);
            int col = Integer.parseInt(splittedStr[3]);
            String player = isOTurn ? "O" : "X";
            String userMsg = row + " " + col + " " + player;
            if (!client.equals(SubscribersList.get(0).getClient()))
                return;
            if (gameBoard[row][col] != null)
                return;
            if (isFullBoard()) {
                sendToAllClients("over" + userMsg);
                return;
            }
            sendToAllClients("update board " + userMsg);
            gameBoard[row][col] = player;
            isOTurn = !isOTurn;
            if (player.equals("O")) {
                sendToAllClients("X");
            } else {
                sendToAllClients("O");
            }
            if (checkWin()) {
                sendToAllClients("done" + userMsg);
                reset();
            }
        }

    }

    private void reset() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameBoard[i][j] = "";
            }
        }
    }

    private boolean isFullBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if ((gameBoard[i][0] != null && !gameBoard[i][0].isEmpty() && gameBoard[i][0].equals(gameBoard[i][1]) && gameBoard[i][1].equals(gameBoard[i][2]))) {
                return true;
            }
            if (gameBoard[0][i].equals(gameBoard[1][i]) && gameBoard[1][i].equals(gameBoard[2][i])) {
                return true;
            }
        }
        if (gameBoard[0][0].equals(gameBoard[1][1]) && gameBoard[1][1].equals(gameBoard[2][2])) {
            return true;
        }
        return gameBoard[0][2].equals(gameBoard[1][1]) && gameBoard[1][1].equals(gameBoard[2][0]);
    }

    public void sendToAllClients(String message) {
        try {
            for (SubscribedClient subscribedClient : SubscribersList) {
                subscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
