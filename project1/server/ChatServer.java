package server;

import java.net.*;
import java.security.NoSuchAlgorithmException;

import tools.ciphers.AESCipher;

import java.io.*;

/**
 * This class is for the server of the group chat. Essentially positions itself in the "middle" 
 * of the group chat. All clients send messages to the server, so that they can be 
 * distributed to the rest of the members in the group chat. Creates a ChatClientHandler object
 * to deal with each new connection.
 * 
 * Responsabilities:
 * (1) Start the server program
 * (2) Open the connection socket
 * (3) Create a ChatClientHandler for each new connection in separate threads
 * (4) Create unique new AES cipher 
 * 
 * @author Anthony Arseneau
 * @version March 4, 2024
 * Networks project
 */
public class ChatServer {
    // Class constants
    static final private int PORT_NUM = 12000;

    // Instance variables
    private ServerSocket serverSocket; // The socket the server is using to transfer data
    private AESCipher aesCipher; // AES cipher for encrypting messages

    /**
     * Constructor
     */
    public ChatServer() throws NoSuchAlgorithmException, IOException {
        // Open socket
        this.serverSocket = new ServerSocket(PORT_NUM);
        // Create a new AES cipher with new secret key and initialization vector
        this.aesCipher = new AESCipher();
        this.aesCipher.generateKey();
        this.aesCipher.generateIv();
    }

    /**
     * Method to initialize the server for the group chat
     */
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) { // While the server socket is not closed, loop
                // Accept new clients that want to connect to the server
                Socket socket = serverSocket.accept();
                System.out.println("A new client has been connected");
                ChatClientHandler chatClientHandler = new ChatClientHandler(socket, aesCipher);

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

    /********* Start Server Program *********/
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ChatServer chatServer = new ChatServer(); // Create new chat server
        chatServer.startServer(); // Start the server
    }
}