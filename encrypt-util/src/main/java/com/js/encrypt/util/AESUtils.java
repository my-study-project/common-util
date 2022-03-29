package com.js.encrypt.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;


/**
 * AES
 */
public class AESUtils {
    public static final String KEY_ALGORITHM = "AES";
    public static final String ENCODING = "utf-8";

    /**
     * 生成 AES key
     *
     * @return
     */
    public static String generateAESKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        byte[] keyExternal = key.getEncoded();
        return Base64.encodeBase64String(keyExternal);
    }

    /**
     * 使用AES KEY 加密
     *
     * @param content
     * @param key
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            byte[] bytesKey = Base64.decodeBase64(key);
            SecretKeySpec secretKey = new SecretKeySpec(bytesKey, KEY_ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            byte[] byteContent = content.getBytes(ENCODING);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 加密
            byte[] result = cipher.doFinal(byteContent);
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 使用AES KEY解密
     *
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) {
        try {
            byte[] bytesKey = Base64.decodeBase64(key);
            SecretKeySpec secretKey = new SecretKeySpec(bytesKey, KEY_ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // 解密
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result);
        } catch (Exception e) {
        }
        return null;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String content = "test";
        String key = generateAESKey();
//        LOGGER.info("加密前：" + content);
        System.out.println(content);
        // 加密
        String encryptResult = encrypt(content, key);
        System.out.println(encryptResult);
        // 解密
        String decryptResult = decrypt(encryptResult, key);
//        LOGGER.info("解密后：" + new String(decryptResult));
        System.out.println(decryptResult);
    }
}

