package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class is for the invalid credentials GUI message that users get when putting wrong credentials
 * 
 * Responsibilities:
 * (1) Pop up a window to show the user they gave invalid credentials
 * (2) Give the option to stop the application
 * (3) Give the option to retry to give correct credentials
 * 
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks project
 */
public class InvalidView extends JFrame {
    // Class constants
    private static final Font MAIN_FONT = new Font("Helvetica Neue", Font.PLAIN, 16); // The main font for the UI
    private static final Font MAIN_FONT_BOLD = new Font("Helvetica Neue", Font.BOLD, 16); // The main font for the UI in bold
    private static final int WINDOW_WIDTH = 275; // Width of the window
    private static final int WINDOW_HEIGHT = 150; // Height of the window
    private static final int MAX_WINDOW_WIDTH = 275; // Max width of the window
    private static final int MAX_WINDOW_HEIGHT = 150; // Max height of the window
    private static final int BORDER_PADDING = 10; // Border padding around the main pane

    /**
     * Method that initializes the components of the GUI
     */
    public void initialize() {

        /********** Invalid Label **********/
        JLabel lbVerifying = new JLabel("Inavlid Credentials", SwingConstants.CENTER);
        lbVerifying.setFont(MAIN_FONT_BOLD);

        /********** Buttons Panel *********/
        // Leave button
        MyButton btnLeave = new MyButton("Leave");
        btnLeave.setFont(MAIN_FONT);
        btnLeave.setOpaque(true);
        btnLeave.setRolloverEnabled(true);
        // Leave button action
        btnLeave.addActionListener(new ActionListener() {
            // Just close the window
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Retry button
        MyButton btnRetry = new MyButton("Retry");
        btnRetry.setFont(MAIN_FONT);
        btnRetry.setOpaque(true);
        btnRetry.setRolloverEnabled(true);
        // Retry button action
        btnRetry.addActionListener(new ActionListener() {
            // Create a new authenticator and close this window
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthenticatorView authenticatorView = new AuthenticatorView();
                authenticatorView.initialize();
                dispose();
            }
        });

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 5, 5));
        buttonsPanel.add(btnLeave);
        buttonsPanel.add(btnRetry);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
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
}