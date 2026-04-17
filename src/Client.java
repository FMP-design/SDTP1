import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 5432);

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);

            new Thread(() -> {
                try {
                    String answer;
                    while ((answer = in.readLine()) != null) {
                        System.out.println(answer);
                    }
                }catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();

            while (true) {
                String message = sc.nextLine();
                out.println(message);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}