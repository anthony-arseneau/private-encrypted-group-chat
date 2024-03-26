package src;
import java.net.*;
import java.io.*;

/**
 * This class is for a client that connects to the server.
 * @author Anthony Arseneau
 * @version March 4, 2024
 * Networks project
 */
public class ChatClientListener {
    // Instance variables
    private Socket socket; // The socket of the client is using to transfer data
    private BufferedReader bufferedReader; // Read the received messages from server
    private ChatView chatView;

    /**
     * Constructor
     * @param socket the socket of the client to transfer information
     * @param username the username of this client
     */
    public ChatClientListener(String username, String password) {
        try {
            socket = new Socket("127.0.0.1", 12000); // Current IP address is local for testing
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            chatView = new ChatView(socket, username, password);
            chatView.initialize();
            listenForMessages();
        } catch (IOException e) {
            // Error handling
            closeAll(socket, bufferedReader);
        }
    }

    /**
     * Method to listen for incomming messages
     */
    public void listenForMessages() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = null;
                try {
                    while (socket.isConnected() && response == null) {
                        response = bufferedReader.readLine();
                    }

                    if (response.equals("Invalid Credentials")) {
                        System.out.println("Invalid Credentials");
                        chatView.addText(response);
                        closeAll(socket, bufferedReader);
                    }
                    else {
                        String messageFromGroupChat;
                        while (socket.isConnected()) {
                            messageFromGroupChat = bufferedReader.readLine();
                            chatView.addText(messageFromGroupChat);
                        } 
                    }
                } catch (IOException e) {
                    // Error handling
                    closeAll(socket, bufferedReader);
                }
            }
        }).start();
    }

    /**
     * Close the socket, buffered reader, and buffered writer
     * @param socket the socket the client-server is connected to
     * @param bufferedReader the buffered reader to read the incomming messages
     * @param bufferedWriter the buffered writer to send messages to the rest of the connected clients
     */
    public void closeAll(Socket socket, BufferedReader bufferedReader) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // Error handling
            e.printStackTrace();
        }
    }

    /*
    public void requestAccess() {

    }
    */

    /**
     * Main method to start the client and connect to the group chat
     */
    /*
    public static void main(String[] args) throws IOException {
        // Estalish socket
        Socket socket = new Socket("127.0.0.1", 12000); // Current IP address is local for testing
        System.out.println("Connected");

        // Create client
        ChatClient chatClient = new ChatClient(socket);
        chatClient.listenForMessages();
        chatClient.sendMessage();
    }
    */
}