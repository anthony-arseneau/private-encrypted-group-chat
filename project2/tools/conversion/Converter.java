package tools.conversion;


/**
 * This class performs coversions between hex strings and byte arrays
 * 
 * Responsabilities:
 * (1) Convert a byte array to a hex string
 * (2) Convert a hex string to byte array
 * 
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks project
 */
public class Converter {
    /**
     * Method to convert a byte array to a hex string
     * @param bytes the byte array to cenvert
     * @return the conversion to string hex
     * Code taken from Baeldung: https://www.baeldung.com/java-byte-arrays-hex-strings
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Method to convert a hex string to a byte array
     * @param s the hex string to covert
     * @return the conversion to byte array
     * Code taken from Stackoverflow: https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
