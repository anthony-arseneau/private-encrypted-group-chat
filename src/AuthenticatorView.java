package src;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class is for the authentication window for clients to first connect to the group chat
 * @author Anthony Arseneau
 * @version March 5, 2024
 * Networks project
 */
public class AuthenticatorView extends JFrame {
    // Class constants
    private static final Color LIGHT_GREY = new Color(235, 235, 235); // Light grey color
    private static final Font MAIN_FONT = new Font("Helvetica Neue", Font.PLAIN, 16); // The main font for the UI
    private static final Font MAIN_FONT_BOLD = new Font("Helvetica Neue", Font.BOLD, 16); // The main font for the UI in bold
    private static final Font TF_FONT = new Font("Monospcae", Font.PLAIN, 16); // The text field font for the UI
    private static final int WINDOW_WIDTH = 275; // Width of the window
    private static final int WINDOW_HEIGHT = 325; // Height of the window
    private static final int MAX_WINDOW_WIDTH = 325; // Max width of the window
    private static final int MAX_WINDOW_HEIGHT = 350; // Max height of the window
    private static final int MAX_CHAR_USERNAME = 20; // Maximum length for the username
    private static final int MAX_CHAR_PASSWORD = 20; // Maximum length for the password
    private static final int BORDER_PADDING = 10; // Border padding around the main pane

    // Instance variables
    private JTextField tfUsername, tfPassword; // Textfield for the username and password input
    private JLabel lbVerifying; // Verifying label

    public void initialize() {
        /********** Credentials Panel **********/
        JLabel lbUsername = new JLabel("Enter username: ");
        lbUsername.setFont(MAIN_FONT_BOLD);

        tfUsername = new JTextField();
        tfUsername.setFont(TF_FONT);
        tfUsername.setBackground(LIGHT_GREY);
        tfUsername.setDocument(new JTextFieldLimit(MAX_CHAR_USERNAME));

        JLabel lbPassword = new JLabel("Enter password: ");
        lbPassword.setFont(MAIN_FONT_BOLD);

        tfPassword = new JTextField();
        tfPassword.setFont(TF_FONT);
        tfPassword.setBackground(LIGHT_GREY);
        tfPassword.setDocument(new JTextFieldLimit(MAX_CHAR_PASSWORD));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 1, 5, 2));
        formPanel.setBackground(Color.WHITE);
        formPanel.add(lbUsername);
        formPanel.add(tfUsername);
        formPanel.add(lbPassword);
        formPanel.add(tfPassword);

        /********** Welcome Label **********/
        lbVerifying = new JLabel("", SwingConstants.CENTER);
        lbVerifying.setFont(MAIN_FONT_BOLD);

        /********** Buttons Panel *********/
        MyButton btnClear = new MyButton("Clear");
        btnClear.setFont(MAIN_FONT);
        btnClear.setOpaque(true);
        btnClear.setRolloverEnabled(true);
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                tfUsername.setText("");
                tfPassword.setText("");
                lbVerifying.setText("");
            }
        });

        MyButton btnSubmit = new MyButton("Submit");
        btnSubmit.setFont(MAIN_FONT);
        btnSubmit.setOpaque(true);
        btnSubmit.setRolloverEnabled(true);
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = tfUsername.getText();
                String password = tfPassword.getText();
                lbVerifying.setText("Verifying credentials...");
                ChatClientListener chatClientListener = new ChatClientListener(username, password);
                dispose(); // removes window
                //chatClient.requestAccess();
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 5, 5));
        buttonsPanel.add(btnClear);
        buttonsPanel.add(btnSubmit);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(lbVerifying, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setTitle("SPGC Authenticator");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setMaximumSize(new Dimension(MAX_WINDOW_WIDTH, MAX_WINDOW_HEIGHT));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /*
    public void requestAccess(String username, String password) {
        ChatClient chatClient = new ChatClient(username, password);
        chatClient.listenForMessages();
        ChatView chatView = new ChatView(chatClient);
    }
    */ 
    public static void main(String[] args) {
        AuthenticatorView clientFrame = new AuthenticatorView();
        clientFrame.initialize();
    }
}
