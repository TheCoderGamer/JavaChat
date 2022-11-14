import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket echoSocket = new ServerSocket(portNumber);) {
            while (true) {
                Socket clientSocket = echoSocket.accept();
                System.out.println("Client connected");

                Thread thread = new Thread(() -> {
                    try (
                            PrintWriter socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
                            BufferedReader socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    ) {
                        String inputLine;
                        while ((inputLine = socketIn.readLine()) != null) {
                            socketOut.println(inputLine);
                        }
                    } catch (IOException e) {
                        System.err.println("Client disconnected");
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
