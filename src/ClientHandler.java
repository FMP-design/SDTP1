import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket s;

    public ClientHandler (Socket s) {
        super();
        this.s = s;
    }
    public void run (){
        try {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(s.getInputStream()));

            String mensagem;

            while ((mensagem = in.readLine()) != null) {
                System.out.println("Cliente said: " + mensagem);
            }

            s.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
