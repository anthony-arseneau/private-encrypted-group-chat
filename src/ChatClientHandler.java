package src;

import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This class is for handling all the clients that are connected to the server.
 * It holds a list of all clients that are connected, so that the server can send
 * messages from one client to the other clients. It operates on the server side 
 * of the network.
 * @author Anthony Arseneau
 * @vesion March 4, 2024
 * Networks project
 */
public class ChatClientHandler implements Runnable {
    // Instance variables
    private Socket socket; // The socket the server is using to transfer data
    private BufferedReader bufferedReader; // Read the received messages from that client
    private BufferedWriter bufferedWriter; // Write the received messages from that client to the other clients
    private String chatClientUserName; // The username of that client

    // Class variables
    public static ArrayList<ChatClientHandler> chatClientHandlers = new ArrayList<>(); // List of all connected client handlers

    /**
     * Constructor
     * @param socket the socket of the server to transfer information
     * @throws NoSuchAlgorithmException 
     */
    public ChatClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Read incoming messages
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Write and send messages

            /* RSA decryption */
            RSAEncryption rsaEncryptionServer = new RSAEncryption();
            rsaEncryptionServer.readPrivateKey("Documents/server_private.key");

            String encryptedClientCredentials = bufferedReader.readLine();

            String[] clientCredentials = (rsaEncryptionServer.decrypt(encryptedClientCredentials)).split(" ");

            String username = clientCredentials[0];
            String password = clientCredentials[1];

            RSAEncryption rsaEncryptionClient = new RSAEncryption();
            rsaEncryptionClient.readPublicKey("Documents/public.key");

            Whitelist whitelist = new Whitelist();
            if (!whitelist.validUser(username, password)) {
                String encryptedResponse = rsaEncryptionClient.encrypt("N");
                //System.out.println("Sending: N as " + encryptedResponse);
                bufferedWriter.write(encryptedResponse);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                System.out.println("New client disconnected due to invalid credentials");
                closeAll(socket, bufferedReader, bufferedWriter);
            }
            else {
                String encryptedResponse = rsaEncryptionClient.encrypt("Y");
                //System.out.println("Sending: Y as " + encryptedResponse);
                bufferedWriter.write(encryptedResponse);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                chatClientUserName = username;
                chatClientHandlers.add(this);
                System.out.println("New client successfully connected to the groupchat");
                //broadCastMessage(chatClientUserName + " has entered the chat!");
            }

        } catch (IOException e) {
            // Error handling
            closeAll(socket, bufferedReader, bufferedWriter);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient.equals("--stop connection--")) {
                    removeClientHandler();
                }
                else {
                    broadCastMessage(messageFromClient);
                }
            } catch (IOException e) {
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
                //if (!chatClientHandler.chatClientUserName.equals(this.chatClientUserName)) { // If not this current client // Actually want to send back to sender
                // Send message to all other clients
                chatClientHandler.bufferedWriter.write(messageToSend);
                chatClientHandler.bufferedWriter.newLine();
                chatClientHandler.bufferedWriter.flush();
                //}
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