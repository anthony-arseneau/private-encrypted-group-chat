package server;

import java.net.*;
import java.security.NoSuchAlgorithmException;

import tools.ciphers.AESCipher;

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
    private AESCipher aesCipher;

    /**
     * Constructor
     * @param serverSocket the socket of the server to transfer information
     * @throws NoSuchAlgorithmException 
     */
    public ChatServer(ServerSocket serverSocket) throws NoSuchAlgorithmException {
        this.serverSocket = serverSocket;
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
        ServerSocket serverSocket = new ServerSocket(12000); // Create a server socket on port 12000
        ChatServer chatServer = new ChatServer(serverSocket); // Create a ChatServer instance
        chatServer.startServer(); // Start the server
    }
}