package src;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;

/**
 * This class connects and sends messages to the server
 * 
 * Responsabilities:
 * (1) Connect to the server
 * (2) Send creadentials to the server
 * (3) Send messages to the server
 * 
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks project
 */
public class ChatClientSender {
    // Instance variables
    private Socket socket; // The socket of the client is using to transfer data
    private BufferedWriter bufferedWriter; // Write messages to the server
    private MessageDigest digest; // SHA-256 message digest

    /**
     * Constructor
     * @param socket the same socket the listener is using to connect to the server
     * @param username the username of the client
     * @param password the passwaord of the client
     */
    public ChatClientSender(Socket socket, String username, String password) throws InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        try {
            // Get SHA-256 hash function
            digest = MessageDigest.getInstance("SHA-256");

            // Hash username and get hex string value
            byte[] usernameHashed = digest.digest(username.getBytes(StandardCharsets.UTF_8));
            String usernameHashedHex = Converter.bytesToHex(usernameHashed);

            // Hash password and get hex string value
            byte[] passwordHashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String passwordHashedHex = Converter.bytesToHex(passwordHashed);

            this.socket = socket; // Current IP address is local for testing
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Get RSA encryption
            RSAEncryption rsaEncryptionServer = new RSAEncryption();
            rsaEncryptionServer.readPublicKey("Documents/server_public.key");

            // Set the message for sending the creadentials
            String privateMessage = usernameHashedHex + " " + passwordHashedHex;
            // Encrypt the message with the server public key
            String encryptedMessage = rsaEncryptionServer.encrypt(privateMessage);

            // Send the message to the server
            sendMessage(encryptedMessage);

            // If there a connection, send to the groupchat that this user joined the conversation
            if (socket.isConnected()) {
                sendMessage(username + " has joined the chat");
            }

        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            // Error handling
            closeAll(); // Close connection
        }
    }

    /**
     * Method to send messages to the server
     */
    public void sendMessage(String message) {
        try {
            if (socket.isConnected()) {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            // Error handling
            closeAll();
        }
    }

    /**
     * Close the socket, buffered reader, and buffered writer
     * @param socket the socket the client-server is connected to
     * @param bufferedReader the buffered reader to read the incomming messages
     * @param bufferedWriter the buffered writer to send messages to the rest of the connected clients
     */
    public void closeAll() {
        try {
            // Make sure all exist before closing
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // Error handling
            e.printStackTrace();
        }
    }
}
