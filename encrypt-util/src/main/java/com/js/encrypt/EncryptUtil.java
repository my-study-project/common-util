package com.js.encrypt;

import com.js.encrypt.encryption.RsaEncryption;
import com.js.encrypt.enums.EncodingEnum;
import com.js.encrypt.enums.KeyFlagEnum;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密工具类
 * <p>
 * md5加密出来的长度是32位
 * <p>
 * sha加密出来的长度是40位
 *
 */
public final class EncryptUtil {

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptPublicKey(byte[] data, String publicKey) throws Exception {
        //使用JDK的util包下的base64实现解码
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 对数据加密
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        //使用Cipher.getInstance加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        //使用JDK的util包下的base64实现解码
        byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        //使用KeyFactory工厂处理私钥
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        //使用Cipher.getInstance解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    //密钥需要注意的是生成密钥时选择格式：PKCS8(java适用)
    static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCe1HcHiKzaJdziPwrtmlW72gaDx+0DlhaGphVUwWkmlvWHd6mteVrr7Gs5CHaf8Y9XJbfkoHH8aEWpnhk9hYHy+JuQPYjYAgkK6IVpY69tnRrdrV42+DRPJSwDqfKrqBbYNYo9ddNSyO/uixYJPLIVwdrRTMUu19oeSSIVAvATWQIDAQAB";
    static String privateKey = "MIICcwIBADANBgkqhkiG9w0BAQEFAASCAl0wggJZAgEAAoGBAJ7UdweIrNol3OI/Cu2aVbvaBoPH7QOWFoamFVTBaSaW9Yd3qa15WuvsazkIdp/xj1clt+SgcfxoRameGT2FgfL4m5A9iNgCCQrohWljr22dGt2tXjb4NE8lLAOp8quoFtg1ij1101LI7+6LFgk8shXB2tFMxS7X2h5JIhUC8BNZAgMBAAECf2W/toEdDZ6yos5NlLKiLEorYgEKEsw5WjToMMIbJUGTc7dU8V4wYA7DZe0jftr35NvvTd8o6dzI79e5cHH5FUWKXqEldMqeTzFfPLPgyAaevxDvyBO3Z6mCkIA1ptNLfj47JTdpabc2al6qFZfJfOro+ufT/aIE1pWoLF/GARECQQD2rLyhBiRZfFf9bnUAWaG3RNE5i7Ef7t64DBZO9frZe660a8Xk8Yxzi7KMviq9aIY6LgsV1Ake2W97CcbGNtBrAkEApNWV7YwqLRM8yBO3VIflzsbtuk3RjicwjxzJzkLhR91xvWQDLx50L7kt0e1SNcuVJw3Xr0yGfPNAw4vE9FQMSwJAewyn+9tIfqscaXuUOdx8YyOdCwu4C6nox/6fkjv6KkscVzv7t70WxvzE0Jh8UYe2jYcyWG0xL4Zfqgyyb2YgiQJAKxltyl8L6B1Pl0EQfpnKDPcW0c/nKzQ0DjeIzNXP8eqFAvBTpM5hstjIkktrY4WHyl5kNwHbaHByTq8NIJWZYQJASWfwM30dJ5YAVq3ZMYkY0AeyQuJptdW4m3UJZWb2HyNU/KfPnGJ+OEO2A7XaFeRfO177RUvCqiwPAL4Y4pFvdw==";
    //加密算法RSA  //使用默认的算法
    public static final String KEY_ALGORITHM = "RSA";
    //RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;
    //RSA最大解密密文大小  ; 需要注意，如果用的是2048位密钥，这里需要改为256 ; 1024密钥是 128
    private static final int MAX_DECRYPT_BLOCK = 256 / 2;

    public static void main(String[] args) throws Exception {
        test();
    }

    static void test() throws Exception {
        // http JSON 请求 公钥加密私钥解密 私钥加签 公钥验签
        RsaEncryption privateRsa =
                RsaEncryption.of(privateKey.getBytes(EncodingEnum.DEFAULT_ENCODING.getEncoding()), KeyFlagEnum.PRIVATE_KEY);
        RsaEncryption publicRsa =
                RsaEncryption.of(Base64.getDecoder().decode(publicKey), KeyFlagEnum.PUBLIC_KEY);
//        Map<String, String> params = JSON.parseObject("inputStr", new TypeReference<Map<String, String>>() {
//        });
//        String sign = privateRsa.sign(params);
//        Map<String, String> params1 = JSON.parseObject("decryptData", new TypeReference<HashMap<String, String>>() {
//        }, Feature.OrderedField);
//        boolean checkSignFlag = publicRsa.checkSign(params, params.get("sign"));
        String test100 = publicRsa.encrypt("{\"test100\":100}");
        System.out.println(test100);
//        String decrypt = privateRsa.decrypt(test100);
//        System.out.println(decrypt);

//        System.out.println("—— 公钥加密 —— 私钥解密 ——");
//        String source = "小程序服务端加解密!";//需要加密的原文
//        System.out.println("加密前原文:  " + source);
//        byte[] data = source.getBytes();
//        byte[] encodedData = encryptPublicKey(data, publicKey);//加密
//        //没有处理过的密文字符串
//        System.out.println("加密后内容:  " + new String(encodedData));
//        //加密后问密文需要使用Base64编码然后转换成string返回前端
//        String encodedDataStr = new String(Base64.getEncoder().encode(encodedData));
//        System.out.println("---:base64处理:  " + encodedDataStr);
//        byte[] decodedData = decryptPrivateKey(encodedData, privateKey);//解密
//        String str = new String(decodedData);
//        System.out.println("解密后内容:  " + str);//小程序服务端加解密!
    }

}