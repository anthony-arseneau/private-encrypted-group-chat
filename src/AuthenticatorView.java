package src;

import java.awt.*;
import java.awt.event.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class is the authentication window for clients when they first connect to the group chat
 * 
 * Responsabilities:
 * (1) Prompt user for username and password
 * (2) Clear text fields with a button
 * (3) Authenticate credentials with the server
 * (4) Create a chat client listener that kickstarts the client connection to the server
 * 
 * @author Anthony Arseneau
 * @version March 28, 2024
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
    private JTextField tfUsername; // Text field for the username input
    private JPasswordField tfPassword; // Password field for the password input
    private JLabel lbVerifying; // Verifying label

    /**
     * Method that initializes the components of the GUI
     */
    public void initialize() {
        /********** Credentials Panel **********/
        // Username label
        JLabel lbUsername = new JLabel("Enter username: ");
        lbUsername.setFont(MAIN_FONT_BOLD);

        // Username textfield
        tfUsername = new JTextField();
        tfUsername.setFont(TF_FONT);
        tfUsername.setBackground(LIGHT_GREY);
        tfUsername.setDocument(new JTextFieldLimit(MAX_CHAR_USERNAME));

        // Password label
        JLabel lbPassword = new JLabel("Enter password: ");
        lbPassword.setFont(MAIN_FONT_BOLD);

        // Password text field
        tfPassword = new JPasswordField();
        tfPassword.setFont(TF_FONT);
        tfPassword.setBackground(LIGHT_GREY);
        //tfPassword.setEchoChar('*'); // Uncomment to have stars instead of dots for the password input
        tfPassword.setDocument(new JTextFieldLimit(MAX_CHAR_PASSWORD));

        /********* Form Panel **********/
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 1, 5, 2));
        formPanel.setBackground(Color.WHITE);
        formPanel.add(lbUsername);
        formPanel.add(tfUsername);
        formPanel.add(lbPassword);
        formPanel.add(tfPassword);

        /********** Verifying Label **********/
        lbVerifying = new JLabel("", SwingConstants.CENTER);
        lbVerifying.setFont(MAIN_FONT_BOLD);

        /********** Buttons Panel *********/
        // Clear button
        MyButton btnClear = new MyButton("Clear");
        btnClear.setFont(MAIN_FONT);
        btnClear.setOpaque(true);
        btnClear.setRolloverEnabled(true);
        // Button action
        btnClear.addActionListener(new ActionListener() {
            // Clear the text fields
            @Override
            public void actionPerformed(ActionEvent e) {
                tfUsername.setText("");
                tfPassword.setText("");
                lbVerifying.setText("");
            }
        });

        // Submit button
        MyButton btnSubmit = new MyButton("Submit");
        btnSubmit.setFont(MAIN_FONT);
        btnSubmit.setOpaque(true);
        btnSubmit.setRolloverEnabled(true);
        // Button action
        btnSubmit.addActionListener(new ActionListener() {
            // Get the credentials from the text fields and verify with server
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get username
                String username = tfUsername.getText();
                // Get password
                char[] passwordChars = tfPassword.getPassword();
                String password = "";
                for (int i = 0; i < passwordChars.length; i++) { // Put all chars in a string
                    password = password + passwordChars[i];
                }
                lbVerifying.setText("Verifying credentials...");

                // Create a chat client listener with the given credentials
                try {
                    ChatClientListener chatClientListener = new ChatClientListener(username, password);
                    chatClientListener.listenForMessages();
                } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e1) {
                    e1.printStackTrace();
                }

                // Remove window
                dispose();
            }
        });

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 5, 5));
        buttonsPanel.add(btnClear);
        buttonsPanel.add(btnSubmit);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(lbVerifying, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Set window aspects
        setTitle("SPGC Authenticator");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setMaximumSize(new Dimension(MAX_WINDOW_WIDTH, MAX_WINDOW_HEIGHT));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /********* Start Client Program *********/
    public static void main(String[] args) {
        AuthenticatorView clientFrame = new AuthenticatorView();
        clientFrame.initialize();
    }
}