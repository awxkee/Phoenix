package com.github.dozzatq.phoenix.Guard67;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dxfb on 23.05.2017.
 */

public class PhoenixAESKey {

    private static final byte[] INITIAL_IV = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private SecretKeySpec secretKeySpec;

    public PhoenixAESKey(String key) {
        this(key, INITIAL_IV);
    }

    private byte[] salt;

    public PhoenixAESKey(String key, byte[] salt)
    {
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Phoenix AES Key NoSuchAlgorithmException");
        }
        this.salt = salt;
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 65536, 256);
        SecretKey secretKey = null;
        try {
            if (skf != null) {
                secretKey = skf.generateSecret(spec);
            }
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException("Phoenix AES Key Invalid Specification");
        }
        if (secretKey != null) {
            this.secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
        }
    }

    public String importSecretKey()
    {
        return new String(Base64.encode(secretKeySpec.getEncoded(), Base64.DEFAULT));
    }

    private PhoenixAESKey(String secret, byte[] salt, boolean flag)
    {
        secretKeySpec = new SecretKeySpec(Base64.encode(secret.getBytes(), Base64.DEFAULT), "AES");
        this.salt = salt;
    }

    public static PhoenixAESKey fromSecretKey(String secret, byte[] salt)
    {
        return new PhoenixAESKey(secret, salt, true);
    }

    protected SecretKey getSecretKey()
    {
        return secretKeySpec;
    }

    protected IvParameterSpec getInitialVector()
    {
        return new IvParameterSpec(salt);
    }

    public String getIV() {
        return new String(Base64.encode(salt, Base64.DEFAULT));
    }

    public static byte[] getSalt() throws Exception {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[20];
        sr.nextBytes(salt);
        return salt;
    }
}
