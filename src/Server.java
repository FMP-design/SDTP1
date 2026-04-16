import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket ss;

    public Server() {
        try {
            ss = new ServerSocket(5432);
            System.out.println("Server Started at port 5432");

            GameState game = new GameState();
            game.setupGame();

            while (true) {
                Socket socket = ss.accept();
                System.out.println("PLAYER ON!");

                ClientHandler jogador = new ClientHandler(socket, game);

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String args[]) {
        Server server = new Server();
    }
}