import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String clientName;

    public Client() {
        setTitle("Chat Client");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        inputPanel.add(messageField, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        clientName = JOptionPane.showInputDialog(this, "Enter your name:");
        setTitle("Chat Client - " + clientName);

        try {
            socket = new Socket("localhost", 8888);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Send the client's name to the server as a separate message
            writer.println("NAME:" + clientName);

            new ReceiveThread().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.startsWith("CMD:CLR")) {
            chatArea.setText(""); // Clear the chat area
            messageField.setText(""); // Clear the text field
        } else if (message.startsWith("CMD:PORT")) {
            printServerPortID();
            messageField.setText("");
        } else if (message.startsWith("CMD:PM ") || message.startsWith("CMD:PM")) {
            writer.println(message);
            messageField.setText(""); // Clear the text field
        } else if (!message.isEmpty()) {
            writer.println(message);
            messageField.setText(""); // Clear the text field
        }
    }


    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    private void printServerPortID() {
        chatArea.append("Server Port ID: " + socket.getLocalPort() + "\n");
    }

}