package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {

	private char[][] board = new char[3][3]; // Shared board
	//private int currentPlayer = 1; // 1 for Player 1 ('X'), 2 for Player 2 ('O')
	//private int clientsConnected = 0;
	private int clientsReady = 0;

	private SubscribedClient player1 = null,player2 = null,currPlayer = null;

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
		try {
			if (player1 == null) {
				player1 = new SubscribedClient(client);
				client.sendToClient("PLAYER 1");
				System.out.println("Player 1 connected.");
				currPlayer = player1 ;
			} else if (player2 == null) {
				player2 = new SubscribedClient(client);
				client.sendToClient("PLAYER 2");
				currPlayer = player1; // Start with Player 1
				System.out.println("Player 2 connected.");
			} else {
				client.sendToClient("GAME_FULL"); // reject additional clients
				client.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();

		if (msgString.startsWith("MOVE")) {
			String[] parts = msg.toString().split(" ");
			String[] indices = parts[1].split(",");
			int row = Integer.parseInt(indices[0]);
			int col = Integer.parseInt(indices[1]);
			char mark = (currPlayer == player1) ? 'X' : 'O';

			synchronized (this) {
				if (board[row][col] == ' ') {
					board[row][col] = mark;

					if (checkWinner() == 'X'){
						sendToAllClients("Winner X");
					}
					else if (checkWinner() == 'O') {
						sendToAllClients("Winner O");
					}
					 else if (checkDraw()) {
						sendToAllClients("Draw");
					}


					// Broadcast the move and next turn
					try {
						sendToAllClients("MOVE " + row + "," + col + " " + (currPlayer == player1 ? "X" : "O"));
						sendToAllClients("TURN " + (currPlayer == player1 ? "2" : "1"));
						currPlayer = currPlayer == player1 ? player2 : player1;

						/***/
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		if (msgString.startsWith("client ready")) {
			System.out.println("simpler server msg add client");
			clientsReady++;

			/* reset to == 2 */
			if (clientsReady == 2) {
				sendToAllClients("startGame");
				resetBoard();
				System.out.println("simpler server");
			}
		}

	}
	private char checkWinner() {
		// Check rows
		for (int i = 0; i < 3; i++) {
			if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
				return board[i][0];
			}
		}

		// Check columns
		for (int i = 0; i < 3; i++) {
			if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
				return board[0][i];
			}
		}

		// Check diagonals
		if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
			return board[0][0];
		}
		if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
			return board[0][2];
		}

		// No winner
		return ' ';
	}

	private boolean checkDraw() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == ' ') {
					return false;
				}
			}
		}
		return true;
	}



}