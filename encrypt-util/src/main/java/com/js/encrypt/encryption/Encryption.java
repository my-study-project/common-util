package com.js.encrypt.encryption;


import java.util.Map;

public interface Encryption {

    /**
     * 加密
     *
     * @param plainText : 明文
     * @return
     * @throws RuntimeException
     */
    String encrypt(String plainText) throws RuntimeException;

    /**
     * 解密
     *
     * @param cipherText : 密文
     * @return
     * @throws RuntimeException
     */
    String decrypt(String cipherText) throws RuntimeException;

    /**
     * 签名（使用RSA私钥）
     *
     * @param params：待签名的参数
     * @return
     * @throws RuntimeException
     */
    String sign(Map<String, String> params) throws RuntimeException;

    /**
     * 验签（使用RSA公钥）
     *
     * @param params：待验证的参数
     * @param sign：签名
     * @return true: 验签成功，false: 验签失败
     * @throws RuntimeException
     */
    boolean checkSign(Map<String, String> params, String sign) throws RuntimeException;
}
