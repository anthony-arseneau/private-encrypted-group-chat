import java.net.*;
import java.io.*;

/**
 * This class is for the server of the group chat.
 * Essentially positions itself in the "middle" of the group chat. 
 * All clients send messages to the server, so that it can they can
 * be distributed to the rest of the members in the group chat.
 * @author Anthony Arseneau
 * @version March 4, 2024
 * Networks project
 */
public class ChatServer {
    // Instance variables
    private ServerSocket serverSocket; // The socket the server is using to transfer data

    /**
     * Constructor
     * @param serverSocket the socket of the server to transfer information
     */
    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Method to initialize the server for the group chat
     */
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) { // While the server socket is not closed, loop
                // Accept new clients that want to connect to the server
                Socket socket = serverSocket.accept();
                System.out.println("A new client has been connected!");
                ChatClientHandler chatClientHandler = new ChatClientHandler(socket);

                // Start a new thread for the new connected client
                Thread thread = new Thread(chatClientHandler);
                thread.start();
            }
        } catch (IOException e) {
            // Error handling
            e.printStackTrace();
            closeServerSocket();
        }
    }

    /**
     * Method to close the socket of the server
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // Error handling
            e.printStackTrace();
        }
    }

    /**
     * Main method to start the group chat server
     * @param args not used
     * @throws IOException if there is an error with the server socket
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12000); // Create a server socket on port 12000
        ChatServer chatServer = new ChatServer(serverSocket); // Create a ChatServer instance
        chatServer.startServer(); // Start the server
    }
}