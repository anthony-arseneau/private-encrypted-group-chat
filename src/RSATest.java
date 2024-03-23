package src;
import java.awt.GraphicsEnvironment;
import java.security.*;

public class RSATest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPairGenerator keysGenerator = KeyPairGenerator.getInstance("RSA");
        keysGenerator.initialize(2048);
        KeyPair keyPair = keysGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        for(String name : fontNames) {
            System.out.println(name);
        }
    }
}