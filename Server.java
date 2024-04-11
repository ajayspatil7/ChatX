import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 8888;
    static List<Handler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String firstMessage = reader.readLine();

                // Check if the first message contains the client's name
                if (firstMessage.startsWith("NAME:")) {
                    String clientName = firstMessage.substring(5);
                    System.out.println("Client name: " + clientName);

                    Handler handler = new Handler(clientSocket, clientName);
                    clients.add(handler);
                    handler.start();
                } else {
                    System.out.println("Invalid client name format");
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void broadcastMessage(String message, Handler sender) {
        for (Handler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    static void removeClient(Handler handler) {
        clients.remove(handler);
    }

    static void blockClient(Handler sender, String clientName) {
        for (Handler client : clients) {
            if (client.clientName.equals(clientName)) {
                sender.blockedClients.add(client);
                client.blockedClients.add(sender);
                sender.sendMessage("You have blocked " + clientName);
                client.sendMessage("You have been blocked by " + sender.clientName);
            }
        }
    }

    static void unblockClient(Handler sender, String clientName) {
        for (Handler client : clients) {
            if (client.clientName.equals(clientName)) {
                sender.blockedClients.remove(client);
                client.blockedClients.remove(sender);
                sender.sendMessage("You have unblocked " + clientName);
                client.sendMessage("You have been unblocked by " + sender.clientName);
            }
        }
    }
}