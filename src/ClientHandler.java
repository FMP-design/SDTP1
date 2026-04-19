import java.io.*;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;


public class ClientHandler extends Thread {
    private Socket skt;
    private Server server;
    //Indica se é a vez do jogador na ronda
    private boolean myTrun = false;
    //Indica se o jogador já jogou na ronda
    private boolean played = false;
    private String lastGuess;
    private PrintWriter out;
    private int id;

    public ClientHandler(Socket skt, Server server, int id) throws IOException {
        super();
        this.skt = skt;
        this.server = server;
        this.id = id;
        out = new PrintWriter(skt.getOutputStream(), true);
        start();
    }

    public synchronized void setMyTurn(boolean myTrun) {
        this.myTrun = myTrun;
    }

    public synchronized boolean hasPlayed() {
        return played;
    }

    public synchronized String getLastGuess() {
        return lastGuess;
    }

    public void closeConnection() {
        try {
            skt.close();
        } catch (IOException e) {
            System.out.println("Error closing client socket");
        }
    }

    //Reinialização do estado do jogador para uma nova ronda
    public synchronized void resetTurn() {
        myTrun = false;
        played = false;
        lastGuess = null;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public int getId() {
        return id;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));

            String message;
            //Ciclo de leitura de mensagens do cliente
            while ((message = in.readLine()) != null) {
                synchronized (this) {
                    //Se não for o meu tunro -> IGNORAR
                    if (!myTrun) {
                        continue;
                    }
                    //Guarda jogada recebida para o servidor
                    message = message.trim();
                    if (message.isEmpty()) continue;
                    if (message.startsWith("GUESS ")) {
                        lastGuess = message.substring(6);
                    }
                    played = true;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (!skt.isClosed()) {
                    skt.close();
                }
            } catch (IOException e) {

            }
        }
    }
}
