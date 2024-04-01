package ciphers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import conversion.Converter;

/**
 * This class is to implement RSA encryption
 * 
 * Responsabilities:
 * (1) Create paired public and private keys
 * (2) Save keys in files
 * (3) Encrypt / decrypt strings
 * 
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks Project
 * 
 * Help from Baeldung: https://www.baeldung.com/java-rsa
 */
public class RSACipher {
    // Instance variables
    private PublicKey publicKey;
    private PrivateKey privateKey;

    /**
     * Method to create a pair of public and private keys
     * @param publicFilePath the file path to save the public key
     * @param privateFilePath the file path to save the private key
     */
    public void createKeyPair(String publicFilePath, String privateFilePath) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        // Create a generator for creating keys
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048); // Key size of 2048 bits long

        // Get the keys from the generated
        KeyPair pair = generator.generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();

        // Save keys in respective files
        writePublicKey(publicFilePath);
        writePrivateKey(privateFilePath);
    }

    /**
     * Method to write public to respective file
     * @param publicFilePath file path to save the key
     */
    public void writePublicKey(String publicFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(publicFilePath)) {
            fos.write(publicKey.getEncoded()); // Write the bits to file
        }
    }

    /**
     * Method to write private to respective file
     * @param privateFilePath file path to save the key
     */
    public void writePrivateKey(String privateFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(privateFilePath)) {
            fos.write(privateKey.getEncoded()); // Write bits to file
        }
    }

    /**
     * Method to encrypt a string
     * @param secretMessage the message to encrypt
     * @return the encrypted message
     */
    public String encrypt(String secretMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Create a RSA cipher
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey); // Encrypt mode

        // Encrypt message
        byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8); // Get the byte array of the message string
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes); // Encrypt

        // Convert byte array to a legible hex string
        String encryptedMessage = Converter.bytesToHex(encryptedMessageBytes);
        
        // Return encrypted message
        return encryptedMessage;
    }

    /**
     * Method to decrypt a string
     * @param encryptedMessage the message to decrypt
     * @return the decrypted message
     */
    public String decrypt(String encryptedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Create a RSA cipher
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey); // Decrypt mode

        // Convert hex string to byte array
        byte[] encryptedMessageBytes = Converter.hexStringToByteArray(encryptedMessage);

        // Decrypt message
        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes); // Decrypt
        String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8); // Get the message string of the byte array

        // Return decrypted message
        return decryptedMessage;
    }

    /**
     * Method to read a public key from a file
     * @param filePath the file path of the public key
     * @return the public key
     */
    public PublicKey readPublicKey(String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Get the file
        File publicKeyFile = new File(filePath);
        // Read the bytes
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

        // Save and return the public key
        setPublicKey(publicKeyBytes);
        return publicKey;
    }

    /**
     * Method to read a private key from a file
     * @param filePath the file path of the private key
     * @return the private key
     */
    public PrivateKey readPrivateKey(String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Get the file
        File privateKeyFile = new File(filePath);
        // Read the data
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());

        // Save and return the private key
        setPrivateKey(privateKeyBytes);
        return privateKey;
    }

    /**
     * Method that sets the public key given the key in a byte array
     * @param publicKeyBytes the byte array of the public key
     */
    public void setPublicKey(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Create a key factory for RSA
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // Read the bytes for a public key
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        // Save the public key
        this.publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * Method that sets the private key given the key in a byte array
     * @param privateKeyBytes the byte array of the private key
     */
    public void setPrivateKey(byte[] privateKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Create a key factory for RSA
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // Read the bytes for a private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        // Save the private key
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    /*
     * Main method that will create RSA key pairs given file names to save them in
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RSACipher rsaEncryption = new RSACipher();
        rsaEncryption.createKeyPair("Documents/server_public.key", "Documents/server_private.key");
    }
}
