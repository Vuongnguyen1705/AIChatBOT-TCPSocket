package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class CipherUtils {

    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static RC2_Cipher rc2 = new RC2_Cipher();
    private static int keyBit = 63;

    public static void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String enCryptKey(String key1, String value) {
        int[] K = rc2.KeyExpansion(key1, key1.length(), keyBit);
        return rc2.Encrypt(K, value);
    }

    public static String deCryptKey(String key1, String value) {
        int[] K = rc2.KeyExpansion(key1, key1.length(), keyBit);
        return rc2.Decrypt(K, value);
    }

    public static String enString(String strToEncrypt, String secret) {
        try {
            System.out.println("secret encrypt " + secret);

            setKey(secret.trim());
            System.out.println("secretKey encrypt" + secretKey);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String deString(String strToDecrypt, String secret) {
        try {
            System.out.println("secret decrypt " + secret);
            setKey(secret.trim());
            System.out.println("secretKey decrypt" + secretKey);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
