import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java EchoClient <port number>");
            System.exit(1);
        }

        InetAddress inetAddress = InetAddress.getLocalHost();

        int portNumber = Integer.parseInt(args[0]);

        try (Socket socket = new Socket(inetAddress, portNumber);
             PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            // Create listener
            Thread listener = new Thread(() -> {
                try (BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = socketIn.readLine()) != null) {
                        System.out.println("Server: " + inputLine);
                    }
                } catch (IOException e) {
                    System.err.println("Client disconnected");
                }
            });
            listener.start();

            // Manage input
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                socketOut.println(userInput);
            }
        }

    }
}
