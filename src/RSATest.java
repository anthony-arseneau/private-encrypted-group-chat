package src;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSATest {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        RSAEncryption rsa = new RSAEncryption();
        rsa.readPublicKey("Documents/public.key");
        rsa.readPrivateKey("Documents/private.key");
        String secretMessage = "Hello World!";
        System.out.println("Secret message is: " + secretMessage);
        String encryptedMessage = rsa.encrypt(secretMessage);
        System.out.println("Encrypted message is: " + encryptedMessage);
        String decryptedMessage = rsa.decrypt(encryptedMessage);
        System.out.println("Decrypted message is: " + decryptedMessage);
    }
}