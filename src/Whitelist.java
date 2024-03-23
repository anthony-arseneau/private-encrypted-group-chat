package src;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.HashMap;

public class Whitelist {
    // Class constants
    private static final String whitelist = "whitelist.txt";

    // Instance variables
    private HashMap<String, String> users;
    private MessageDigest digest;
    
    public Whitelist() throws NoSuchAlgorithmException, IOException {
        digest = MessageDigest.getInstance("SHA-256");
        users = new HashMap<>();
        readWhitelist();
    }

    public void addToWhitelist() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        boolean looping = true;
        while (looping) {
            System.out.println("Type \"done\" to stop adding names");
            System.out.print("New username: ");
            input = reader.readLine();
            if (input.equals("done")) looping = false;
            else {
                String newUsername = input;
                System.out.print("New password: ");
                input = reader.readLine();
                String newPassword = input;
                addUser(newUsername, newPassword);
            }
        }
        reader.close();
        printWhitelist();
    }

    public HashMap<String, String> getWhitelist() {
        return users;
    }

    private void printWhitelist() throws FileNotFoundException {
        File whitelistFile = new File(whitelist);
        PrintWriter writer = new PrintWriter(whitelistFile);
        for (String user : users.keySet()) {
            String password = users.get(user);
            writer.println(user + " " + password);
        }
        writer.close();
    }

    private void addUser(String username, String password) {
        // Hash username and get hex string value
        byte[] usernameHashed = digest.digest(username.getBytes(StandardCharsets.UTF_8));
        String usernameHashedHex = bytesToHex(usernameHashed);

        // Hash password and get hex string value
        byte[] passwordHashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String passwordHashedHex = bytesToHex(passwordHashed);

        // Check to see if username already exists
        if (!users.containsKey(usernameHashedHex)) {
            users.put(usernameHashedHex, passwordHashedHex);
        } else {
            System.out.println("Username already exits");
        }
    }

    private void readWhitelist() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(whitelist));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] entry = line.split(" ");
            String username = entry[0];
            String password = entry[1];
            users.put(username, password);
        }
        reader.close();
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        Whitelist whitelist = new Whitelist();
        whitelist.addToWhitelist();
    }
}