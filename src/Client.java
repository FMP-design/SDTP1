import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 5432);

            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);

            while (true) {
                String msg = sc.nextLine();
                out.println(msg);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}