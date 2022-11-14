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

        Socket socket = null;
        int portNumber = Integer.parseInt(args[0]);
        String username;


        // Connect to server
        try {
            socket = new Socket(inetAddress, portNumber);
        } catch (IOException e) {
            System.err.println("Could not connect to server");
            System.exit(1);
            socket.close();
        }

        System.out.printf("Connected to server %s on port %d%n", inetAddress, portNumber);
        try (PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            // Username
            System.out.println("Enter username: ");
            username = stdIn.readLine();
            socketOut.println(username);

            System.out.println("*** Welcome " + username + " ***");


            // Create listener
            Thread listener = new Thread(() -> {
                String inputLine;
                try {
                    while ((inputLine = socketIn.readLine()) != null) {
                        System.out.println("[Server] " + inputLine);
                    }
                } catch (IOException e) {
                    System.err.println("Client disconnected");
                    System.exit(1);
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
