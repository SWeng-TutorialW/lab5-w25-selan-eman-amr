package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;

import static java.lang.System.exit;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private  char[][] board = new char[3][3];
	public int playerNumber = 0; // Player 1 or 2
	private  int currentTurn = 1;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	private void resetBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = ' '; // Empty board
			}
		}
	}

	@Override
	protected void handleMessageFromServer(Object msg) {

		if (msg.toString().startsWith("GAME_FULL")) {
			System.out.println("GAME_FULL");
			exit(1);
		}
		if (msg.toString().startsWith("PLAYER")) {
			this.setPlayerNumber(Integer.parseInt(msg.toString().split(" ")[1]));
			System.out.println("PLAYER " + this.getPlayerNumber());
		}

		if (msg.toString().startsWith("Winner")) {
			EventBus.getDefault().post(msg.toString());
		}
		if (msg.toString().equals("Draw")) {
			EventBus.getDefault().post(msg.toString());
		}

		if (msg.toString().startsWith("startGame")) {
			EventBus.getDefault().post("startGame");
			System.out.println("simple client startGame");
		}
		if (msg.toString().startsWith("MOVE")) {
			System.out.println("simple client MOVE");
			String[] parts = msg.toString().split(" ");
			String[] indices = parts[1].split(",");
			int row = Integer.parseInt(indices[0]);
			int col = Integer.parseInt(indices[1]);
			char mark = parts[2].charAt(0);
			System.out.println("simple client move current mark is" + mark);

			// Update the local board
			board[row][col] = mark;

			// Post an event to update the GUI
			EventBus.getDefault().post(new MoveEvent(row, col, mark));
		}

		if (msg.toString().startsWith("TURN")) {
			currentTurn = Integer.parseInt(msg.toString().split(" ")[1]);
			EventBus.getDefault().post("TURN " + currentTurn);
		}
		if (msg.toString().equals("INVALID_MOVE")) {
			System.out.println("Invalid move! Try again.");
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			//client = new SimpleClient("localhost", 3000);
			client = new SimpleClient("192.168.137.1", 3000);
		}
		return client;
	}

	public int getCurrentTurn() {
		return currentTurn;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}
	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public void sendMoveToServer(int row, int col) {
		try {
			sendToServer("MOVE " + row + "," + col);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
