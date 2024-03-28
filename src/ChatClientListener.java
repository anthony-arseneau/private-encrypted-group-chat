package src;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
    private RSAEncryption rsaEncryptionClient;

    /**
     * Constructor
     * @param socket the socket of the client to transfer information
     * @param username the username of this client
     * @throws InvalidKeySpecException 
     * @throws NoSuchAlgorithmException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws NoSuchPaddingException 
     * @throws InvalidKeyException 
     */
    public ChatClientListener(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        try {
            socket = new Socket("127.0.0.1", 12000); // Current IP address is local for testing
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            /* RSA decryption */
            rsaEncryptionClient = new RSAEncryption();
            rsaEncryptionClient.readPublicKey("Documents/public.key");
            rsaEncryptionClient.readPrivateKey("Documents/private.key");
            chatView = new ChatView(socket, username, password);
            chatView.initialize();
        } catch (IOException e) {
            // Error handling
            closeAll(socket, bufferedReader);
        }
    }

    /**
     * Method to listen for incomming messages
     * @throws InvalidKeySpecException 
     * @throws NoSuchAlgorithmException 
     */
    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("here1");
                    String encryptedResponse = null;
                    while (socket.isConnected() && encryptedResponse == null) {
                        encryptedResponse = bufferedReader.readLine();
                    }
                    System.out.println("here2");
                    //System.out.println("responseHex: " + responseHex);
                    /* RSA decryption */
                    System.out.println("The encrypted response is: " + encryptedResponse);
                    String response = rsaEncryptionClient.decrypt(encryptedResponse);
                    System.out.println("The response is: " + response);

                    if (response.equals("N")) {
                        InvalidView invalidView = new InvalidView();
                        invalidView.initialize();
                        System.out.println("Invalid Credentials");
                        chatView.destroy();
                        closeAll(socket, bufferedReader);
                    }
                    else {
                        chatView.setVisible(true);
                        String messageFromGroupChat;
                        while (socket.isConnected()) {
                            messageFromGroupChat = bufferedReader.readLine();
                            chatView.addText(messageFromGroupChat);
                        } 
                    }
                } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                    // Error handling
                    e.printStackTrace();
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