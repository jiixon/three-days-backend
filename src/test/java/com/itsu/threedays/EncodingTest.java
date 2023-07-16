package com.itsu.threedays;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;

public class EncodingTest {
    public static void main(String[] args) {

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(1);
        encryptor.setPassword("password");

        String plainText = "plaintext";
        String encryptedText = encryptor.encrypt(plainText);
        String decryptedText = encryptor.decrypt(plainText);
        System.out.println("Enc = " + encryptedText);
        System.out.println("Dec = " + decryptedText);
    }
}
