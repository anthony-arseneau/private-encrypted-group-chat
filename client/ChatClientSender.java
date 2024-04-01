package client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.*;
import tools.ciphers.RSACipher;
import tools.conversion.Converter;

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
     * @throws InvalidAlgorithmParameterException 
     */
    public ChatClientSender(Socket socket, String username, String password) throws InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
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

            // Get server RSA encryption
            RSACipher serverRSACipher = new RSACipher();
            serverRSACipher.readPublicKey("ClientDocuments/server_public.key");

            // Get client RSA encryption
            RSACipher clientRSACipher = new RSACipher();
            clientRSACipher.readPublicKey("ClientDocuments/public.key");

            // Set the message for sending the creadentials
            String privateMessage = usernameHashedHex + " " + passwordHashedHex;
            // Encrypt the message with the server public key
            String encryptedMessage = serverRSACipher.encrypt(privateMessage);

            // Send the message to the server
            sendMessage(encryptedMessage + " " + Base64.getEncoder().encodeToString(clientRSACipher.getPublicKey().getEncoded()));

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
