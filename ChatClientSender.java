import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatClientSender {
        private Socket socket; // The socket of the client is using to transfer data
        private BufferedWriter bufferedWriter; // Write messages to the server
        private String username;

        public ChatClientSender(Socket socket, String username) {
            this.username = username;
            try {
                this.socket = socket; // Current IP address is local for testing
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                sendMessage(username);
            } catch (IOException e) {
                closeAll(socket, bufferedWriter);
            }
        }

        public void sendUsername(String username) {
            try {
                if (socket.isConnected()) {
                    bufferedWriter.write(username);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            } catch (IOException e) {
                // Error handling
                closeAll(socket, bufferedWriter);
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
                closeAll(socket, bufferedWriter);
            }
        }

        /**
         * Close the socket, buffered reader, and buffered writer
         * @param socket the socket the client-server is connected to
         * @param bufferedReader the buffered reader to read the incomming messages
         * @param bufferedWriter the buffered writer to send messages to the rest of the connected clients
         */
        public void closeAll(Socket socket, BufferedWriter bufferedWriter) {
            try {
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
