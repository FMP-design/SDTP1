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
            //Inicializa servidor TCP
            ss = new ServerSocket(5432);
            //Tempo máximo de espera por jogadores no "LOBBY"
            ss.setSoTimeout(20000);
            System.out.println("Waiting for players (20s)...");

            //Cria/ Inicializa o jogo
            GameState game = new GameState();
            game.setupGame();

            //REQUIRIMENTO - Máximo 4 jogadores
            while (players.size() < 4) {
                try {
                    Socket socket = ss.accept();
                    System.out.println("PLAYER ON!");
                    broadcast("Waiting for more players (20s)...");

                    ClientHandler player = new ClientHandler(socket, this, players.size() + 1);
                    players.add(player);
                    player.sendMessage("----------WELCOME " + player.getPlayerId() + " " + players.size());

                    //Ajuste no timeout para quando tivermos 2 jogadores esperar por mais
                    if (players.size() >= 2 && ss.getSoTimeout() != 15000) {
                        ss.setSoTimeout(15000);
                        broadcast("Min players reached! Waiting for more... (15s)");
                    }
                } catch (SocketTimeoutException e) {
                    if (players.size() < 2) {
                        System.out.println("Game cancelled!! Not enough players!");
                        return;
                    }
                    System.out.println("Timeout!");
                    break;
                }
            }
            broadcast("----------GAME-STARTED!----------");
            broadcast("----------SECRET-WORD: [" + game.getMaskDisplay() + "]");

            //Ciclo principal do jogo (Termina quando houver vitória ou derrota)
            while (!game.gameOver()) {
                ClientHandler player = players.get(currentPlayer);
                //Define jogador atual como ativo
                player.setMyTurn(true);
                broadcast("----------Player " + player.getPlayerId() + " is now playing!");
                player.sendMessage("----------YOUR-TURN----------");
                String guess = null;
                long startTime = System.currentTimeMillis();

                //Espera jogada ou dá timeout da ronda
                while (!player.hasPlayed()) {
                    if (System.currentTimeMillis() - startTime > 30000) {
                        player.sendMessage("Time OUT!");
                        //Penalização em caso de não responder dentro do tempo
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
                        player.sendMessage("----------CORRECT-GUESS----------");
                    } else {
                        player.sendMessage("----------WRONG-GUESS----------");
                    }
                }

                //Imprimi e atualiza o estado o jogo para todos os jogadores
                gameState(game);
                //Avança para o proximo jogador
                currentPlayer = (currentPlayer + 1) % players.size();
            }
            //Resultado final do jogo
            if (game.won()) {
                broadcast("----------WIN----------");
            } else {
                broadcast("----------LOSE-WORD WAS: " + game.getSecretWord());
            }
            //Encerra todas as ligações (Servidor - Cliente)
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
        broadcast("------------------------------");
        broadcast("WORD: [" + game.getMaskDisplay() + "]");
        broadcast(game.getHangman());
        broadcast("USED LETTERS: " + game.getWrongLetter());
        broadcast("------------------------------");
    }

    public static void main(String args[]) {
        Server server = new Server();
    }
}