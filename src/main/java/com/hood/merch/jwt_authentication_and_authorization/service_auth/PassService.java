//package com.hood.merch.security.service_auth;
//
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.PBEKeySpec;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.KeySpec;
//
//@Service
//public class PassService {
//
//    public byte[] addPassword(String password) throws NoSuchAlgorithmException {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[16];
//        random.nextBytes(salt);
//
//        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//
//        try {
//            byte[] hash = factory.generateSecret(spec).getEncoded();
//            return hash;
//        } catch (InvalidKeySpecException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
