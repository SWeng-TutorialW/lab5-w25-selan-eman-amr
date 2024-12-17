package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {

	private char[][] board = new char[3][3]; // Shared board
	private int currentPlayer = 1; // 1 for Player 1 ('X'), 2 for Player 2 ('O')
	private int clientCount = 0;


	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
		
	}

	/**/
	private void resetBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = ' '; // Empty board
			}
		}
	}

	@Override
	protected void clientConnected(ConnectionToClient client) {
		clientCount++;
		System.out.println("Client connected. Total clients: " + clientCount);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();

		 if(msgString.startsWith("add client")){
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			clientCount++;
			try {
				if (clientCount == 2) {
					sendToAllClients("startGame");
					System.out.println("simpler server");
					// client.sendToClient("startGame");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(msgString.startsWith("remove client")){
			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}
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
