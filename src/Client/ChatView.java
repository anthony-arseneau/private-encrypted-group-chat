package src.Client;

import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import javax.crypto.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class is for the chat window for clients to communicate with other authorized clients
 * 
 * Responsabilities:
 * (1) Display the conversation
 * (2) Take user input and send it to the client sender when "Send" is pressed
 * 
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks project
 */
public class ChatView extends JFrame {
    // Class constants
    private static final Color RED = new Color(218, 37, 58); // Darker sea color
    private static final Color LIGHTER_RED = new Color(222, 59, 78); // Darker sea color
    private static final Color DARKER_RED = new Color(196, 33, 52); // Darker sea color
    private static final Color LIGHT_BLUE = new Color(225, 235, 255); // Darker sea color
    private static final Font MAIN_FONT_BOLD = new Font("Helvetica Neue", Font.BOLD, 16); // The main font for the UI in bold
    private static final Font TF_FONT = new Font("Monospcae", Font.PLAIN, 16); // The text field font for the UI
    private static final int WINDOW_WIDTH = 425; // Width of the window
    private static final int WINDOW_HEIGHT = 600; // Height of the window
    private static final int MAX_WINDOW_WIDTH = 650; // Max width of the window
    private static final int MAX_WINDOW_HEIGHT = 750; // Max height of the window
    private static final int MAX_CHAR_MESSAGE = 50; // Maximum length for the message to send
    private static final int BORDER_PADDING = 10; // Border padding around the main pane
    private static final int S_BORDER_PADDING = 5; // Border padding around the main pane


    // Instance variables
    private JTextField tfMessage; // Textfield for user to write their message
    private JTextArea textArea; // Area where messages will show up
    private String message;
    private ChatClientSender chatClientSender;
    private Socket socket;
    private String username;
    private String password;

    /**
     * Constructor
     * @param socket the socket that will be used to send messages to the server
     * @param username the username of the user
     * @param password the password of the user
     */
    public ChatView(Socket socket, String username, String password) {
        this.socket = socket;
        this.username = username;
        this.password = password;
    }

    public void initialize() throws InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // Create a chat client sender to be able to send messages
        chatClientSender = new ChatClientSender(socket, username, password);

        /********** Scroll Area **********/
        textArea = new JTextArea(10, 15);
        textArea.setBorder(new EmptyBorder(S_BORDER_PADDING, S_BORDER_PADDING, S_BORDER_PADDING, S_BORDER_PADDING));
        textArea.setFont(TF_FONT);
        textArea.setEditable(false);
        textArea.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        /********** Text Field Message & Send Button **********/
        // Text field
        tfMessage = new JTextField();
        tfMessage.setFont(TF_FONT);
        tfMessage.setBackground(LIGHT_BLUE);
        tfMessage.setDocument(new JTextFieldLimit(MAX_CHAR_MESSAGE));

        // Send button
        MyButton btnSend = new MyButton("⇪"); // Nicely interchangable with these symbole: ↵, 
        btnSend.setForeground(Color.WHITE);
        btnSend.setFont(MAIN_FONT_BOLD);
        btnSend.setBorder(new EmptyBorder(1, 0, 3, 0));
        btnSend.setBackground(RED);
        btnSend.setHoverBackgroundColor(LIGHTER_RED);
        btnSend.setPressedBackgroundColor(DARKER_RED);
        btnSend.setRolloverEnabled(true);
        btnSend.setFocusable(false);
        // Make it possible to press the ENTER key to activate the button
        btnSend.registerKeyboardAction(btnSend.getActionForKeyStroke(
                                      KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                                      KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                                      JComponent.WHEN_FOCUSED);
        // Button action
        btnSend.addActionListener(new ActionListener() {
            // Send the message in the text field to the server (only if non-empty field)
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the message in the textfield
                message = tfMessage.getText();
                if(!message.isBlank()) { // Only send if non-empty
                    chatClientSender.sendMessage(username + ": " + message);
                }
                // Make the text field empty
                tfMessage.setText("");
            }
        });

        // Panel to hold text field and send button
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(Color.GRAY);
        messagePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Layout of elements in the panel
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.8;
        messagePanel.add(tfMessage, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 0.20;
        messagePanel.add(btnSend, c);
        
        /********** Main panel of the group chat ********/
        JPanel mainPanel = new JPanel();
        
        // Set layout
        mainPanel.setBorder(new EmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(messagePanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Window title
        setTitle("Secure Private Group Chat");
        // When clicking X to leave:
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                // Send to the group chat that the user left
                chatClientSender.sendMessage(username + " has left the chat");
                // Tell the server to close connection
                chatClientSender.sendMessage("--stop connection--");
                destroy(); // End client process
                System.exit(0); // Stop process
            }
           });
        // Set window layout
        setLocationRelativeTo(null);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setMaximumSize(new Dimension(MAX_WINDOW_WIDTH, MAX_WINDOW_HEIGHT));
        setVisible(false); // First, the window is invisible until user is authenticated
        getRootPane().setDefaultButton(btnSend);
    }

    /**
     * Method to close connection of the sender connection to the server (Client listener will close connection as well when server connection closes)
     * Remove this window
     */
    public void destroy() {
        chatClientSender.closeAll();
        dispose();
    }

    /**
     * Method to display messages to the chat area
     * @param message the message to print on the view
     */
    public void addText(String message) {
        textArea.append(message + "\n\r");
    }
    
    /*
    public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        ChatView chatView = new ChatView(new Socket("127.0.0.1", 12000), "user1", "password123");
        chatView.initialize();
    }
    */
}
