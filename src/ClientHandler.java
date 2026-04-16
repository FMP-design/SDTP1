import java.io.*;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;


public class ClientHandler extends Thread {
    private Socket skt;
    private GameState game;

    public ClientHandler(Socket skt, GameState game) {
        super();
        this.skt = skt;
        this.game = game;
        start();


    }

    public void run() {
        try {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(skt.getInputStream()));


            PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
            out.println("START: ");
            out.println("MASK: " + game.getMaskDisplay());
            out.println("TRIES: " + game.getTriesLeft());

            String message;

            while ((message = in.readLine()) != null && !game.gameOver()) {

                String guess = message.trim();

                if (guess.isEmpty()) {
                    out.println("Empty guess, try again");
                    continue;
                }

                boolean result;

                if (guess.length() == 1) {
                    result = game.guessLetter(guess.charAt(0));
                } else {
                    result = game.guessWord(guess);
                }

                if (!result) {
                    out.println("Wrong Guess!");
                }

                out.println("MASK: " + game.getMaskDisplay());
                out.println("TRIES: " + game.getTriesLeft());
                out.println("USED: " + game.getWrongLetter());
            }

            if (game.won()) {
                out.println("WIN!");
            } else if (game.lost()) {
                out.println("LOSE - Winnig word was :" + game.getSecretWord());
            }

            skt.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
