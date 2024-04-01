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
 * This clas is to implement AES encryption
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
    private static final String algorithm = "AES/CBC/PKCS5Padding";

    // Instance variable
    private SecretKey secretKey;
    private IvParameterSpec iv;
    
    /**
     * Method to create a key
     */
    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE);
        SecretKey key = keyGenerator.generateKey();
        this.secretKey = key;
        return key;
    }

    /**
     * Method to create an Initialization Vector (IV)
     * @return Initialization Vector
     */
    public IvParameterSpec generateIv() {
        byte[] iv = new byte[KEY_SIZE / BITS_IN_BYTE];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        this.iv = ivParameterSpec;
        return this.iv;
    }

    public void writeSecretKey(String secretFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(secretFilePath)) {
            fos.write(secretKey.getEncoded()); // Write the bits to file
        }
    }

    public void writeIV(String ivFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(ivFilePath)) {
            fos.write(iv.getIV()); // Write the bits to file
        }
    }

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
     * @param input
     * @param key
     * @param iv
     * @return
     */
    public String encrypt(String input) 
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * Method to decrypt message
     * @param cipherText
     * @param key
     * @param iv
     * @return
     */
    public String decrypt(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
            .decode(cipherText));
        return new String(plainText);
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    public IvParameterSpec getIV() {
        return this.iv;
    }

    public SecretKey setSecretKey(String encodedSecretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedSecretKey);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return this.secretKey = originalKey;
    }

    public IvParameterSpec setIV(String encodedIV) {
        byte[] decodedIV = Base64.getDecoder().decode(encodedIV);
        IvParameterSpec originalIV = new IvParameterSpec(decodedIV);
        return this.iv = originalIV;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        AESCipher aesCipher = new AESCipher();
        aesCipher.generateKey();
        aesCipher.generateIv();
        String privateMsg = "Hello World!";
        System.out.println("The private message is: " + privateMsg);
        String encryptedMsg = aesCipher.encrypt(privateMsg);
        System.out.println("Encrypted message: " + encryptedMsg);
        String decryptedMessage = aesCipher.decrypt(encryptedMsg);
        System.out.println("Decrypted message: " + decryptedMessage);
    }
}