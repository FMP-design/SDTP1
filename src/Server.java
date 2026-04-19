import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket ss;
    private int currentPlayer = 0;
    private List<ClientHandler> players = new ArrayList<>();

    public Server() {
        try {
            ss = new ServerSocket(5432);
            ss.setSoTimeout(15000);
            System.out.println("Waiting for players (15s)...");

            GameState game = new GameState();
            game.setupGame();

            while (players.size() < 4) {
                try {
                    Socket socket = ss.accept();
                    System.out.println("PLAYER ON!");

                    ClientHandler player = new ClientHandler(socket, this);
                    players.add(player);

                    if (players.size() >= 2 && ss.getSoTimeout() != 10000) {
                        ss.setSoTimeout(10000);
                        System.out.println("Min players reached! Waiting for more...");
                    }
                } catch (SocketTimeoutException e) {
                    if (players.size() < 2) {
                        System.out.println("Game cancelled!!");
                        return;
                    }
                    System.out.println("Timeout!");
                    break;
                }
            }
            broadcast("Game Started!");
            broadcast("WORD: " + game.getMaskDisplay());

            while (!game.gameOver()) {
                ClientHandler player = players.get(currentPlayer);
                player.setMyTurn(true);
                player.sendMessage("Your Turn");
                broadcast("Player: " + currentPlayer + "'s Turn");
                String guess = null;
                long startTime = System.currentTimeMillis();

                while (!player.hasPlayed()) {
                    if (System.currentTimeMillis() - startTime > 30000) {
                        player.sendMessage("Time OUT!");
                        game.wrongMove();
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }

                guess = player.getLastGuess();
                player.resetTurn();

                if (guess == null || guess.isEmpty()) {
                    game.wrongMove();
                } else {
                    boolean rlt = game.guess(guess);

                    if (rlt) {
                        player.sendMessage("Correct Guess!");
                    } else {
                        player.sendMessage("Wrong Guess!");
                    }
                }

                gameState(game);
                currentPlayer = (currentPlayer + 1) % players.size();
            }
            if (game.won()) {
                broadcast("WIN");
            } else {
                broadcast("LOSE |WORD WAS - " + game.getSecretWord() + ".");
            }
            shutdown();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void broadcast(String message) {
        for (ClientHandler player : players) {
            player.sendMessage(message);
        }
    }

    public void shutdown() {
        try {
            for (ClientHandler player : players) {
                player.sendMessage("Game Over. Disconnecting...");

            }

            Thread.sleep(200);

            for (ClientHandler player : players) {
                player.closeConnection();
            }
            ss.close();
            System.out.println("Server closed.");

        } catch (IOException | InterruptedException e) {
            System.out.println("Error shutting down server.");
        }
    }

    public void gameState(GameState game) {
        broadcast("WORD: " + game.getMaskDisplay());
        broadcast("Tries Left: " + game.getTriesLeft());
        broadcast("Used Letters: " + game.getWrongLetter());
    }

    public static void main(String args[]) {
        Server server = new Server();
    }
}