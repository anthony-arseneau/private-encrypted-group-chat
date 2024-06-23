package tools.ciphers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is to implement AES encryption
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks project
 * 
 * Help from Baeldung: https://www.baeldung.com/java-aes-encryption-decryption
 */
public class AESCipher {
    //Class constant
    private static final int KEY_SIZE = 128; // Key size in bits
    private static final int BITS_IN_BYTE = 8; // Number of bits in a byte
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; // Algorithm to use for AES

    // Instance variable
    private SecretKey secretKey;
    private IvParameterSpec iv;
    
    /**
     * Method to create a key
     */
    public SecretKey generateKey() throws NoSuchAlgorithmException {
        // Get a key generator
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        // Create a key with key size constant
        keyGenerator.init(KEY_SIZE);
        // Generate the key
        SecretKey key = keyGenerator.generateKey();
        // Save and return
        this.secretKey = key;
        return key;
    }

    /**
     * Method to create an Initialization Vector (IV)
     * @return Initialization Vector
     */
    public IvParameterSpec generateIv() {
        // Initialize iv byte array
        byte[] iv = new byte[KEY_SIZE / BITS_IN_BYTE];
        // Get a random sequence of bytes
        new SecureRandom().nextBytes(iv);
        // Create an iv from random byte array
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        // Save and return
        this.iv = ivParameterSpec;
        return this.iv;
    }

    /**
     * Method to write secret key to file
     * @param secretFilePath the file to save the private key
     */
    public void writeSecretKey(String secretFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(secretFilePath)) {
            fos.write(secretKey.getEncoded()); // Write the bits to file
        }
    }

    /**
     * Method to write the Initialization Vector (IV) to file
     * @param ivFilePath the file to save the IV
     */
    public void writeIV(String ivFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(ivFilePath)) {
            fos.write(iv.getIV()); // Write the bits to file
        }
    }

    /**
     * Method to read the secret key from file
     * @param filePath the file where the secret key is
     * @return the secret key
     */
    public SecretKey readSecretKey(String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Get the file
        File secretKeyFile = new File(filePath);
        // Read the bytes
        byte[] secretKeyBytes = Files.readAllBytes(secretKeyFile.toPath());
        // Save and return the public key
        return setSecretKey(new String(secretKeyBytes, StandardCharsets.UTF_8));
    }

    /**
     * Method to encrypt message
     * @param input message to encrypt
     * @return the encrypteed message
     */
    public String encrypt(String input) 
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // Get cipher instance
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // Encrypt mode on
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        // Encrypt message
        byte[] cipherText = cipher.doFinal(input.getBytes());
        // Return String value of encrypted message
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * Method to decrypt message
     * @param cipherText the encrypted message to decrypt
     * @return the decrypted message
     */
    public String decrypt(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // Get cipher instance
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // Decrypt mode on
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        // Decrypt message
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        // Return String value of decrypted message
        return new String(plainText);
    }

    /**
     * Getter method to get the secret key
     * @return the secret key
     */
    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    /**
     * Getter method to get the IV
     * @return the IV
     */
    public IvParameterSpec getIV() {
        return this.iv;
    }

    /**
     * Setter method to set the secret key
     * @param encodedSecretKey the String value of secret key to set
     * @return the secret key
     */
    public SecretKey setSecretKey(String encodedSecretKey) {
        // Get byte array value of String
        byte[] decodedKey = Base64.getDecoder().decode(encodedSecretKey);
        // Get the secret key
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        // Set the key
        return this.secretKey = originalKey;
    }

    /**
     * Setter method to set the IV
     * @param encodedIV the String value of IV to set
     * @return the IV
     */
    public IvParameterSpec setIV(String encodedIV) {
        // Get byte array value of String
        byte[] decodedIV = Base64.getDecoder().decode(encodedIV);
        // Get the IV
        IvParameterSpec originalIV = new IvParameterSpec(decodedIV);
        // Set the IV
        return this.iv = originalIV;
    }
}