import java.io.*;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;


public class ClientHandler extends Thread {
    private Socket skt;
    private Server server;
    private boolean myTrun = false;
    private boolean played = false;
    private String lastGuess;
    private PrintWriter out;

    public ClientHandler(Socket skt, Server server) throws IOException {
        super();
        this.skt = skt;
        this.server = server;
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

    public void closeConnection(){
        try{
            skt.close();
        } catch (IOException e){
            System.out.println("Error closing client socket");
        }
    }

    public synchronized void resetTurn() {
        myTrun = false;
        played = false;
        lastGuess = null;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                synchronized (this) {
                    if(!myTrun) {
                        out.println("Wait Your Turn!");
                        continue;
                    }
                    message = message.trim();
                    if(message.isEmpty()) continue;
                    lastGuess = message;
                    played = true;
                }
            }

            skt.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally{
            try{
                if(!skt.isClosed()) {
                    skt.close();
                }
            } catch(IOException e){

            }
        }
    }
}
