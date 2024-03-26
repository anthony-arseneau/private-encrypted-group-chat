package src;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.HashMap;

public class Whitelist {
    // Class constants
    private static final String whitelist = "Documents/whitelist.txt";

    // Instance variables
    private HashMap<String, String> users;
    
    public Whitelist() {
        try {
            users = new HashMap<>();
            readWhitelist();
        } catch (IOException e) {
            System.out.println(e);
        }
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

    private void addUser(String usernameHashedHex, String passwordHashedHex) {
        // Check to see if username already exists
        if (!users.containsKey(usernameHashedHex)) {
            users.put(usernameHashedHex, passwordHashedHex);
        } else {
            System.out.println("Username already exits");
        }
    }

    public boolean validUser(String usernameHashedHex, String passwordHashedHex) {
        if (!users.containsKey(usernameHashedHex)) return false;
        if (!(users.get(usernameHashedHex)).equals(passwordHashedHex)) return false;
        return true;
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

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        Whitelist whitelist = new Whitelist();
        whitelist.addToWhitelist();
    }
}