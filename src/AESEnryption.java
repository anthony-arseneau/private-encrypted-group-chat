package src;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * This clas is to implement AES encryption
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks project
 * 
 * Help from Baeldung: https://www.baeldung.com/java-aes-encryption-decryption
 */
public class AESEnryption {
    //Class constant
    private static final int KEY_SIZE = 128; // Key size in bits
    private static final int BITS_IN_BYTE = 8; // Number of bits in a byte
    private static final String algorithm = "AES/CBC/PKCS5Padding";

    // Instance variable
    private SecretKey secretKey;
    
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
        return new IvParameterSpec(iv);
    }

    /**
     * Method to encrypt message
     * @param input
     * @param key
     * @param iv
     * @return
     */
    public String encrypt(String input, SecretKey key, IvParameterSpec iv) 
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
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
    public String decrypt(String cipherText, SecretKey key, IvParameterSpec iv)
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
            .decode(cipherText));
        return new String(plainText);
    }

}