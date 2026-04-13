import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket ss;

    public Server(){
        try {
            ss = new ServerSocket (5432);
            System.out.println("Server Started at port 5432");

            while (true){
                Socket socket = ss.accept();
                System.out.println("Client ON!");

                ClientHandler client = new ClientHandler(socket);
                client.start();
            }
        }catch ( IOException e){
            System.out.println(e.getMessage());
        }
    }
    public static void main (String args[]){
        Server server = new Server();
    }
}