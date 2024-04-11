import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Handler extends Thread {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    String clientName;

    // Private messaging mode and private group
    private boolean isPrivateMode = false;
    private List<Handler> privateGroup = new ArrayList<>();

    List<Handler> blockedClients = new ArrayList<>();

    public Handler(Socket socket, String name) {
        this.clientSocket = socket;
        this.clientName = name;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            String message;
            while ((message = reader.readLine()) != null) {
                if (message.startsWith("CMD:BLOCK") && message.length() > 10) {
                    handleBlockCommand(message);
                } else if (message.startsWith("CMD:UNBLOCK") && message.length() > 12) {
                    handleUnblockCommand(message);
                } else if (message.startsWith("CMD:PM ")) {
                    handlePMCommand(message);
                } else {
                    String formattedMessage = formatMessage(message);
                    if (isPrivateMode) {
                        broadcastToPrivateGroup(formattedMessage, this);
                    } else {
                        broadcastMessageToUnblockedClients(formattedMessage, this);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.removeClient(this);
        }
    }

    // Method to handle CMD:BLOCK command
    private void handleBlockCommand(String message) {
        String[] clientNames = message.substring(10).trim().split(",");
        for (String name : clientNames) {
            if (name.equals(clientName)) {
                sendMessage("ERROR: You cannot block or unblock yourself!!!");
            } else {
                Server.blockClient(this, name.trim());
            }
        }
    }

    // Method to handle CMD:UNBLOCK command
    private void handleUnblockCommand(String message) {
        String[] clientNames = message.substring(12).trim().split(",");
        for (String name : clientNames) {
            if (name.equals(clientName)) {
                sendMessage("ERROR: You cannot block or unblock yourself!!!");
            } else {
                Server.unblockClient(this, name.trim());
            }
        }
    }

    // Method to handle CMD:PM command
    private void handlePMCommand(String message) {
        String[] parts = message.split(" ", 2);
        if (parts.length < 2) {
            return; // Invalid command
        }
        String subCommand = parts[1].trim();

        if (subCommand.equalsIgnoreCase("ENTER")) {
            isPrivateMode = true;
            sendMessage("Entered private messaging mode.");
        } else if (subCommand.equalsIgnoreCase("LEAVE")) {
            isPrivateMode = false;
            sendMessage("Left private messaging mode.");
        } else if (subCommand.startsWith("ADD ")) {
            handlePMAdd(subCommand.substring(4).trim());
        } else if (subCommand.startsWith("REMOVE ")) {
            handlePMRemove(subCommand.substring(7).trim());
        } else if (subCommand.equalsIgnoreCase("PRINT")) {
            handlePMPrint();
        }
    }

    // Handle adding clients to the private group
    private void handlePMAdd(String names) {
        String[] clientNames = names.split(",");
        for (String name : clientNames) {
            Handler client = findClientByName(name.trim());
            if (client != null && client != this) {
                privateGroup.add(client);
                sendMessage(name.trim() + " added to private group.");
            } else {
                sendMessage(name.trim() + " could not be added.");
            }
        }
    }

    // Handle removing clients from the private group
    private void handlePMRemove(String names) {
        String[] clientNames = names.split(",");
        for (String name : clientNames) {
            Handler client = findClientByName(name.trim());
            if (client != null) {
                privateGroup.remove(client);
                sendMessage(name.trim() + " removed from private group.");
            }
        }
    }

    // Handle printing private mode and group
    private void handlePMPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Private mode: ").append(isPrivateMode).append("\n");
        sb.append("Private group: ");
        if (privateGroup.isEmpty()) {
            sb.append("No clients.");
        } else {
            for (Handler client : privateGroup) {
                sb.append(client.clientName).append(", ");
            }
            // Remove the last comma and space
            sb.setLength(sb.length() - 2);
        }
        sendMessage(sb.toString());
    }

    // Broadcast message to private group
    private void broadcastToPrivateGroup(String message, Handler sender) {
        for (Handler client : privateGroup) {
            if (!client.blockedClients.contains(sender)) {
                client.sendMessage(message);
            }
        }
    }

    // Find client by name
    private Handler findClientByName(String name) {
        for (Handler client : Server.clients) {
            if (client.clientName.equals(name)) {
                return client;
            }
        }
        return null;
    }

    private void broadcastMessageToUnblockedClients(String message, Handler sender) {
        for (Handler client : Server.clients) {
            if (!client.blockedClients.contains(sender) && !sender.blockedClients.contains(client)) {
                client.sendMessage(message);
            }
        }
    }

    private String formatMessage(String message) {
        String clientMode = "SMALL";

        switch (clientMode) {
            case "SMALL":
                return "(" + clientName + ") : " + message;
            case "CAPITAL":
                return "(" + clientName + ") : " + message.toUpperCase();
            case "BOTH":
                return "(" + clientName + ") : " + message.toUpperCase() + "\n(" + clientName + ") : " + message;
            default:
                return "(" + clientName + ") : " + message;
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}
