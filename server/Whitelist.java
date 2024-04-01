package server;

import java.io.*;
import java.util.HashMap;

/**
 * Class that helps keeping a whitelist of authorized users in the groupchat
 * 
 * Responsabilities:
 * (1) Keep a list of authorized users
 * (2) Add users to the white list
 * (3) Print the whitelist to .txt file
 * (4) Validate given credentials
 * 
 * @author Anthony Arseneau
 * @version March 24, 2024
 * Networks project
 */
public class Whitelist {
    // Class constants
    private static final String whitelist = "Documents/whitelist.txt";

    // Instance variables
    private HashMap<String, String> users;
    
    // Constructor
    public Whitelist() {
        try {
            // Create a hash map to keep up with valid users
            users = new HashMap<>();
            // Read the .txt file and save credentials in hash map
            readWhitelist();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Method to add users to the whitelist
     * (1) Run and keep adding names and passwords as needed
     * (2) Write "done" when done adding users
     */
    public void addToWhitelist() throws IOException {
        // Get user input from the terminal
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // Add as many users as desired
        String input = "";
        boolean looping = true;
        while (looping) {
            // Prompt user
            System.out.println("Type \"done\" to stop adding names");
            System.out.print("New username: ");
            // Read input / name
            input = reader.readLine();
            if (input.equals("done")) looping = false; // Done adding users
            else {
                String newUsername = input;
                // Prompt for password
                System.out.print("New password: ");
                // Read password
                input = reader.readLine();
                String newPassword = input;
                // Save credentials
                addUser(newUsername, newPassword);
            }
        }
        reader.close();
        // Save whitelist to .txt file
        printWhitelist();
    }

    /**
     * Getter method to get the whitelist as hash map
     * @return the hash map whitelist
     */
    public HashMap<String, String> getWhitelist() {
        return users;
    }

    /**
     * Method to write the whitelist to .txt file
     */
    private void printWhitelist() throws FileNotFoundException {
        // Read the file
        File whitelistFile = new File(whitelist);
        // Get buffered writer
        PrintWriter writer = new PrintWriter(whitelistFile);
        // Write every hased username and password (each user's credentials is on 
        // one line with a space seperating username and password)
        for (String user : users.keySet()) {
            String password = users.get(user);
            writer.println(user + " " + password);
        }
        writer.close(); // Close writer
    }

    /**
     * Method to add user
     * @param usernameHashedHex the hash value of the username to add
     * @param passwordHashedHex the hash value of the password to add
     */
    private void addUser(String usernameHashedHex, String passwordHashedHex) {
        // Check to see if username already exists
        if (!users.containsKey(usernameHashedHex)) {
            // Add user
            users.put(usernameHashedHex, passwordHashedHex);
        } else {
            // Username already exists
            System.out.println("Username already exits");
        }
    }

    /**
     * Method to validate users
     * @param usernameHashedHex the hash value of the username to add
     * @param passwordHashedHex the hash value of the password to add
     * @return {@code true} if user credentials are valid
     */
    public boolean validUser(String usernameHashedHex, String passwordHashedHex) {
        if (!users.containsKey(usernameHashedHex)) return false;
        if (!(users.get(usernameHashedHex)).equals(passwordHashedHex)) return false;
        return true;
    }

    /**
     * Read .txt whitelist and save credentials in the hash map
     */
    private void readWhitelist() throws IOException {
        // Get buffered reader
        BufferedReader reader = new BufferedReader(new FileReader(whitelist));
        // Read every line
        String line;
        while ((line = reader.readLine()) != null) {
            String[] entry = line.split(" "); // Split line
            String username = entry[0]; // Get hash value of username
            String password = entry[1]; // Get hash value of password
            users.put(username, password); // Save in hash map
        }
        reader.close();

    }

    /*
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        Whitelist whitelist = new Whitelist();
        whitelist.addToWhitelist();
    }
    */
}