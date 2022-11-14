import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private static final ArrayList<PrintWriter> writers = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket echoSocket = new ServerSocket(portNumber)) {
            while (true) {
                Socket clientSocket = echoSocket.accept();

                Thread thread = new Thread(() -> {
                    try (PrintWriter socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
                         BufferedReader socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                    ) {
                        writers.add(socketOut);
                        String username = socketIn.readLine();
                        System.out.println(username + " connected");
                        sendToAll(username + " connected");

                        String inputLine;
                        while ((inputLine = socketIn.readLine()) != null) {
                            sendToAll(username + ": " + inputLine);
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

    public static synchronized void sendToAll(String msg) {
        for (PrintWriter writer : writers) {
            writer.println(msg);
        }
    }

}
