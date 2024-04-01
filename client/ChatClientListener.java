package client;

import tools.ciphers.*;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import java.io.*;

/**
 * This class connects and listens to the server
 * 
 * Responsabilities:
 * (1) Connect to the server
 * (2) Listen for authentication validation
 * (3) If valid credentials, listen for any incoming messages coming from the group chat
 * (4) Tell the chat view to display new messages
 * (5) Show invalid credentials message if necessary
 * 
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks project
 */
public class ChatClientListener {
    // Instance variables
    private Socket socket; // The socket of the client is using to transfer data
    private BufferedReader bufferedReader; // Read the received messages from server
    private ChatView chatView; // Chat view to display the group chat
    private RSACipher clientRSACipher; // Utilize RSA for authentication and symmetric key exchange
    private AESCipher aesCipher;

    /**
     * Constructor
     * @param socket the socket of the client to transfer information
     * @param username the username of this client
     * @throws InvalidAlgorithmParameterException 
     */
    public ChatClientListener(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        try {
            // Create socket and connect to server
            this.socket = new Socket("127.0.0.1", 12000); // Current IP address is local for testing
            // Buffered reader for incoming messages from the server
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read client's RSA priavte key for decryption
            this.clientRSACipher = new RSACipher();
            this.clientRSACipher.readPublicKey("ClientDocuments/public.key");
            this.clientRSACipher.readPrivateKey("ClientDocuments/private.key");

            this.aesCipher = new AESCipher();

            // Create the chat view
            this.chatView = new ChatView(socket, username, password);
            this.chatView.initialize();
        } catch (IOException e) {
            // Error handling
            closeAll(socket, bufferedReader); // Close connection
        }
    }

    /**
     * Method to listen for incomming messages
     */
    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get the first response from the server to know if client is accepted
                    String encryptedResponse = null;
                    while (socket.isConnected() && encryptedResponse == null) {
                        encryptedResponse = bufferedReader.readLine();
                    }

                    // Decrypt response with client RSA private key
                    String response = clientRSACipher.decrypt(encryptedResponse);

                    // If response to authentication is "N", meaning "No":
                    if (response.equals("N")) {
                        // Show invalid creadentials window
                        InvalidView invalidView = new InvalidView();
                        invalidView.initialize();

                        // Delete the chat view and the sender
                        chatView.destroy();
                        // Close connection
                        closeAll(socket, bufferedReader);
                    }
                    // Else, the response is something else, it means meaning "Yes" and we get the secret AES key:
                    else {
                        // Get AES secret key and Initialization Vector (IV) message from server
                        String[] keyIV = response.split(" ");
                        String encodedKey = keyIV[0]; // Get the encoded secret key
                        String encodedIV = keyIV[1]; // Get the encoded IV

                        // Set secret key and IV of AES cipher
                        aesCipher.setSecretKey(encodedKey);
                        aesCipher.setIV(encodedIV);

                        // Save secret key and IV to files
                        aesCipher.writeSecretKey("ClientDocuments/secret_key.txt");
                        aesCipher.writeIV("ClientDocuments/iv.txt");
                        // Set the same cipher of the chat view
                        chatView.setAESCipher(aesCipher);

                        // Make the chat view visible
                        chatView.setVisible(true);

                        // Keep listening for incoming messages until connection cuts
                        String encryptedMessageFromGroupChat;
                        while (socket.isConnected()) {
                            encryptedMessageFromGroupChat = bufferedReader.readLine(); // Get new message
                            String decryptedMessage = aesCipher.decrypt(encryptedMessageFromGroupChat);
                            chatView.addText(decryptedMessage); // Display message in the chat view
                        } 
                    }
                } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                    // Error handling
                    e.printStackTrace();
                    closeAll(socket, bufferedReader); // Close the connection
                } catch (InvalidAlgorithmParameterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
            // Make sure all exist before closing
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
}