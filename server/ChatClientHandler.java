package server;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import tools.ciphers.*;

/**
 * This class is for handling all the clients that are connected to the server. It holds a list of all clients that are connected,
 * so that the server can send messages from one client to the other clients. It operates on the server side of the network.
 * 
 * Responsabilities:
 * (1) Add client to a list of all connections when they successfully connect
 * (2) Verify the credentials of new connections (accepts or refuses them access to chat)
 * (3) Broadcast incoming messages from any user to the rest
 * (4) Remove client from connection list when they leave
 * 
 * @author Anthony Arseneau
 * @vesion March 28, 2024
 * Networks project
 */
public class ChatClientHandler implements Runnable {
    // Class variables
    public static ArrayList<ChatClientHandler> chatClientHandlers = new ArrayList<>(); // List of all connected client handlers

    // Instance variables
    private Socket socket; // The socket the server is using to transfer data
    private AESCipher aesCipher;
    private BufferedReader bufferedReader; // Read the received messages from that client
    private BufferedWriter bufferedWriter; // Write the received messages from that client to the other clients

    /**
     * Constructor
     * @param socket the socket of the server to transfer information
     * @throws NoSuchAlgorithmException 
     */
    public ChatClientHandler(Socket socket, AESCipher aesCipher) {
        try {
            this.socket = socket;
            this.aesCipher = aesCipher;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Read incoming messages
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Write and send messages

            /* Server RSA decryption */
            RSACipher serverRSACipher = new RSACipher(); // Get a RSA cipher
            serverRSACipher.readPrivateKey("ServerDocuments/server_private.key"); // Read the private key

            // Get the new connection message
            String[] connectionMessage = bufferedReader.readLine().split(" ");

            // Get the encrypted credentials
            String encryptedClientCredentials = connectionMessage[0];
            // Get the public key of the client
            String clientPublicKey = connectionMessage[1];

            // Decrypt the credentials
            String[] clientCredentials = (serverRSACipher.decrypt(encryptedClientCredentials)).split(" ");

            // Get the username
            String username = clientCredentials[0];
            // Get the password
            String password = clientCredentials[1];

            /* Client RSA encryption */
            RSACipher clientRSACipher = new RSACipher(); // Get a RSA cipher
            clientRSACipher.setPublicKey(Base64.getDecoder().decode(clientPublicKey)); // Read its given public key

            // Get the whitelist
            Whitelist whitelist = new Whitelist();
            // Check the credentials
            if (!whitelist.validUser(username, password)) { // Not a valid user
                // Send a reply message of "No" (Invalid credentials)
                String encryptedResponse = clientRSACipher.encrypt("N");
                bufferedWriter.write(encryptedResponse);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                System.out.println("New client disconnected due to invalid credentials");
                closeAll(socket, bufferedReader, bufferedWriter); // Close connection with client after failed authentication
            }
            else { // Valid user
                // Get the AES secret key
                SecretKey secretKey = this.aesCipher.getSecretKey();
                // Encode it to be send as a string
                String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

                // Get the AES Initialization Vector (IV)
                IvParameterSpec iv = this.aesCipher.getIV();
                // Encode it to be send as a string
                String encoded_IV = Base64.getEncoder().encodeToString(iv.getIV());

                // Encrypt the response using the client's public key
                String encryptedResponse = clientRSACipher.encrypt(encodedKey + " " + encoded_IV);
                // Send the encrypted secret key
                bufferedWriter.write(encryptedResponse);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                chatClientHandlers.add(this);
                System.out.println("New client successfully connected to the groupchat");
            }

        } catch (IOException e) {
            // Error handling
            closeAll(socket, bufferedReader, bufferedWriter);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException |
        NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to keep listening for incoming messages from the client
     */
    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                // Listen for incoming messages
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient == null) { // If received null message
                    removeClientHandler(); // Remove client
                }
                else if (aesCipher.decrypt(messageFromClient).equals("--stop connection--")) { // If client says to stop connection
                    removeClientHandler(); // Remove client
                }
                else { // Message to send to group chat
                    broadCastMessage(messageFromClient); // broadcast message
                }
            } catch (IOException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
                // Error handling
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }


    /**
     * Method to send message to every other client connected to the server except this current client
     * @param messageToSend the message to send to all other clients
     */
    public void broadCastMessage(String messageToSend) {
        for (ChatClientHandler chatClientHandler : chatClientHandlers) { // For every connected client on the server
            try {
                // Send message to all other clients
                chatClientHandler.bufferedWriter.write(messageToSend);
                chatClientHandler.bufferedWriter.newLine();
                chatClientHandler.bufferedWriter.flush();
            } catch (IOException e) {
                // Error handling
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    /**
     * Method to remove a client from the server indicating that they left the chat
     */
    public void removeClientHandler() {
        if (chatClientHandlers.contains(this)) {
            chatClientHandlers.remove(this); // Remove this current client
        }
    }

    /**
     * Close the socket, buffered reader, and buffered writer
     * @param socket the socket the server-client is connected to
     * @param bufferedReader the buffered reader to read the incomming messages
     * @param bufferedWriter the buffered writer to send messages to the rest of the connected clients
     */
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler(); // First, remove this current connected client
        try {
            // Make sure all exist before closing
            if (bufferedReader != null) {
                bufferedReader.close();
            }
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