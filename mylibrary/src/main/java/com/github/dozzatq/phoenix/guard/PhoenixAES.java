package com.github.dozzatq.phoenix.guard;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by dxfb on 22.05.2017.
 */

public class PhoenixAES {

    private static final String ALGORITHM_AES256 = "AES/CBC/PKCS5Padding";
    private Cipher cipher;

    /**
     * <p>Create AESCipher using a byte[] array as a key</p>
     * <p/>
     * <p><strong>NOTE:</strong> Uses an Initial Vector of 16 0x0 bytes. This should not be used to create strong security.</p>
     */

    private PhoenixAESKey phoenixAESKey;

    public PhoenixAES(@NonNull PhoenixAESKey phoenixAESKey) {
        this.phoenixAESKey = phoenixAESKey;
        try {
            this.cipher = Cipher.getInstance(ALGORITHM_AES256);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("PhoenixAES NoSuchAlgorithmException | NoSuchPaddingException ");
        }
    }

    /**
     * Takes message and encrypts with Key
     *
     * @param message String
     * @return String Base64 encoded
     */
    public String getEncryptedMessage(String message) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

            byte[] encryptedTextBytes = cipher.doFinal(message.getBytes("UTF-8"));

            return new String(Base64.encode(encryptedTextBytes, Base64.DEFAULT));
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            return null;
        }
    }

    /**
     * Takes Base64 encoded String and decodes with provided key
     *
     * @param message String encoded with Base64
     * @return String
     */
    public String getDecryptedMessage(String message) {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);

            byte[] encryptedTextBytes = Base64.decode(message, Base64.DEFAULT);
            byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);

            return new String(decryptedTextBytes);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            return null;
        }
    }

    /**
     * Base64 encoded version of key
     *
     * @return String
     */


    private Cipher getCipher(int encryptMode) throws InvalidKeyException, InvalidAlgorithmParameterException {
        cipher.init(encryptMode, phoenixAESKey.getSecretKey(), phoenixAESKey.getInitialVector());
        return cipher;
    }

}
