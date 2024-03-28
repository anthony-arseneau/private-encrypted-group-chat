package src;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class is for the chat window for clients to communicate with other authorized clients
 * @author Anthony Arseneau
 * @version March 6, 2024
 * Networks project
 */
public class ChatView extends JFrame {
    // Class constants
    private static final Color RED = new Color(218, 37, 58); // Darker sea color
    private static final Color LIGHTER_RED = new Color(222, 59, 78); // Darker sea color
    private static final Color DARKER_RED = new Color(196, 33, 52); // Darker sea color
    private static final Color LIGHT_BLUE = new Color(225, 235, 255); // Darker sea color
    private static final Font MAIN_FONT = new Font("Helvetica Neue", Font.PLAIN, 16); // The main font for the UI
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
    //private String message; // String value for message to send
    private JTextArea textArea;
    private boolean availableMessage;
    private String message;
    private ChatClientSender chatClientSender;
    private Socket socket;
    private String username;
    private String password;

    public ChatView(Socket socket, String username, String password) {
        this.socket = socket;
        this.username = username;
        this.password = password;
    }

    public void initialize() throws InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        chatClientSender = new ChatClientSender(socket, username, password);
        /********** Scroll Area **********/
        textArea = new JTextArea(10, 15);
        textArea.setBorder(new EmptyBorder(S_BORDER_PADDING, S_BORDER_PADDING, S_BORDER_PADDING, S_BORDER_PADDING));
        textArea.setFont(TF_FONT);
        textArea.setEditable(false);
        textArea.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        /********** Text Field Message & Send Button **********/
        tfMessage = new JTextField();
        tfMessage.setFont(TF_FONT);
        tfMessage.setBackground(LIGHT_BLUE);
        tfMessage.setDocument(new JTextFieldLimit(MAX_CHAR_MESSAGE));

        MyButton btnSend = new MyButton("⇪"); // Nicely interchangable with this symbole: ↵
        btnSend.setForeground(Color.WHITE);
        btnSend.setFont(MAIN_FONT_BOLD);
        btnSend.setBorder(new EmptyBorder(1, 0, 3, 0));
        btnSend.setBackground(RED);
        btnSend.setHoverBackgroundColor(LIGHTER_RED);
        btnSend.setPressedBackgroundColor(DARKER_RED);
        btnSend.setRolloverEnabled(true);
        btnSend.setFocusable(false);
        btnSend.registerKeyboardAction(btnSend.getActionForKeyStroke(
                                      KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                                      KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                                      JComponent.WHEN_FOCUSED);
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message = tfMessage.getText();
                if(!message.isBlank()) {
                    chatClientSender.sendMessage(username + ": " + message);
                }
                tfMessage.setText("");
            }
        });

        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(Color.GRAY);
        messagePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

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
        
        JPanel mainPanel = new JPanel();
        
        mainPanel.setBorder(new EmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(messagePanel, BorderLayout.SOUTH);

        add(mainPanel);

        setTitle("Secure Private Group Chat");
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                chatClientSender.sendMessage(username + " has left the chat");
                chatClientSender.sendMessage("--stop connection--");
                chatClientSender.closeAll();
                dispose();
                System.exit(0);
            }
           });
        setLocationRelativeTo(null);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setMaximumSize(new Dimension(MAX_WINDOW_WIDTH, MAX_WINDOW_HEIGHT));
        setVisible(false);
        getRootPane().setDefaultButton(btnSend);
    }

    public void destroy() {
        chatClientSender.closeAll();
        dispose();
    }

    public void addText(String message) {
        textArea.append(message + "\n\r");
    }
    
    public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        ChatView chatView = new ChatView(new Socket("127.0.0.1", 12000), "user1", "password123");
        chatView.initialize();
    }

    public boolean hasAvailableMessage() {
        return availableMessage;
    }

    public void setNoAvailableMessage() {
        availableMessage = false;
    }

    public String getMessage() {
        return message;
    }
    
}
