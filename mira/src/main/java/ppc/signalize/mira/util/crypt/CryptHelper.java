package ppc.signalize.mira.util.crypt;

import android.content.Context;
import android.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class CryptHelper {



    public static PublicKey score_key(Context c) throws IOException {
        BufferedInputStream caInput = new BufferedInputStream(c.getResources().getAssets().open("score_signalize_ws.crt", Context.MODE_PRIVATE));
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());
            X509Certificate ca = (X509Certificate)cf.generateCertificate(caInput);
            return ca.getPublicKey();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            caInput.close();
        }
        return null;
    }
    public static String rsaEncrypt(String plainText, PublicKey key)
            throws NoSuchAlgorithmException, InvalidKeySpecException,
            IOException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchProviderException //
    {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        if(plainText == null || plainText.equals("")) return null;
        if(key == null || key.equals("")) return null;

        byte[] encryptedBytes;

        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");


        cipher.init(Cipher.ENCRYPT_MODE, key);

        encryptedBytes = cipher.doFinal(plainText.getBytes());
        String encrypted = Base64.encodeToString(encryptedBytes,Base64.DEFAULT);
        return encrypted;
    }

    public static Encrypted encrypt(String seed, String cleartext) throws Exception {
        byte[] raw = getRawKey(seed.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(cleartext.getBytes());
        byte[] iv = cipher.getIV();
        return new Encrypted(Base64.encodeToString(raw,Base64.DEFAULT), Base64.encodeToString(encrypted,Base64.DEFAULT), Base64.encodeToString(iv,Base64.DEFAULT));
    }

    public static String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(256, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }


}
