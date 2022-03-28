package com.js.encrypt.encryption;

import com.js.encrypt.enums.EncodingEnum;
import com.js.encrypt.enums.KeyFlagEnum;
import com.js.encrypt.enums.ParamEnum;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.TreeSet;

/**
 * RSA加解密
 * 公钥格式必须是 pkcs#8 pem
 * 私钥格式必须是 x509 der
 * 实例化时需要传入公钥私钥的文件路径
 * rsaEncryptor = new RsaEncryptor("/config/public_key.der", "/config/private_key.pk8");
 */
public class RsaEncryption implements Encryption {

    public static final String KEY_ALGORITHM = "RSA";

    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    /**
     * 加密block需要预留11字节
     */
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";

    private static final int KEYBIT = 2048;

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    private static final int RESERVEBYTES = 11;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private RsaEncryption(String keyPath, KeyFlagEnum keyFlag) {
        if (KeyFlagEnum.PRIVATE_KEY == keyFlag) {
            initPrivateKey(keyPath);
        } else {
            initPublicKey(keyPath);
        }
    }

    private RsaEncryption(File keyFile, KeyFlagEnum keyFlag) {
        if (KeyFlagEnum.PRIVATE_KEY == keyFlag) {
            initPrivateKey(keyFile);
        } else {
            initPublicKey(keyFile);
        }
    }

    private RsaEncryption(byte[] keyBytes, KeyFlagEnum keyFlag) {
        if (KeyFlagEnum.PRIVATE_KEY == keyFlag) {
            initPrivateKey(keyBytes);
        } else {
            initPublicKey(keyBytes);
        }
    }

    private RsaEncryption(String publicKeyPath, String privateKeyPath) {
        initPrivateKey(privateKeyPath);
        initPublicKey(publicKeyPath);
    }

    private RsaEncryption(File publicKeyFile, File privateKeyFile) {
        initPrivateKey(privateKeyFile);
        initPublicKey(publicKeyFile);
    }

    private RsaEncryption(byte[] publicKeyBytes, byte[] privateKeyBytes) {
        initPrivateKey(privateKeyBytes);
        initPublicKey(publicKeyBytes);
    }

    public static RsaEncryption of(String keyPath, KeyFlagEnum keyFlag) {
        return new RsaEncryption(keyPath, keyFlag);
    }

    public static RsaEncryption of(File keyFile, KeyFlagEnum keyFlag) {
        return new RsaEncryption(keyFile, keyFlag);
    }

    public static RsaEncryption of(byte[] keyBytes, KeyFlagEnum keyFlag) {
        return new RsaEncryption(keyBytes, keyFlag);
    }

    public static RsaEncryption of(String publicKeyPath, String privateKeyPath) {
        return new RsaEncryption(publicKeyPath, privateKeyPath);
    }

    public static RsaEncryption of(File publicKeyFile, File privateKeyFile) {
        return new RsaEncryption(publicKeyFile, privateKeyFile);
    }

    public static RsaEncryption of(byte[] publicKeyBytes, byte[] privateKeyBytes) {
        return new RsaEncryption(publicKeyBytes, privateKeyBytes);
    }


    @Override
    public String encrypt(String plainText) throws RuntimeException {
        byte[] plainBytes;
        try {
            plainBytes = plainText.getBytes(EncodingEnum.DEFAULT_ENCODING.getEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        int decryptBlock = KEYBIT / 8;
        int encryptBlock = decryptBlock - RESERVEBYTES;
        int nBlock = (plainBytes.length / encryptBlock);
        if ((plainBytes.length % encryptBlock) != 0) {
            nBlock += 1;
        }
        // 分段加密
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(nBlock * decryptBlock)) {
            Cipher publicCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            publicCipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            for (int offset = 0; offset < plainBytes.length; offset += encryptBlock) {
                int inputLen = (plainBytes.length - offset);
                if (inputLen > encryptBlock) {
                    inputLen = encryptBlock;
                }
                // 得到分段加密结果
                byte[] encryptedBlock = publicCipher.doFinal(plainBytes, offset, inputLen);
                // 追加结果到输出buffer中
                outputStream.write(encryptedBlock);
            }
            outputStream.flush();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(String cipherText) throws RuntimeException {
        byte[] data = Base64.getDecoder().decode(cipherText);
        int decryptBlock = KEYBIT / 8;
        int encryptBlock = decryptBlock - RESERVEBYTES;
        int nBlock = (data.length / decryptBlock);
        // 分段解密
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(nBlock * encryptBlock);) {
            Cipher privateCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            privateCipher.init(Cipher.DECRYPT_MODE, this.privateKey);

            for (int offset = 0; offset < data.length; offset += decryptBlock) {
                // block大小: decryptBlock 或剩余字节数
                int inputLen = (data.length - offset);
                if (inputLen > decryptBlock) {
                    inputLen = decryptBlock;
                }
                // 得到分段解密结果
                byte[] decryptedBlock = privateCipher.doFinal(data, offset, inputLen);
                // 追加结果到输出buffer中
                outputStream.write(decryptedBlock);
            }
            outputStream.flush();
            try {
                return new String(outputStream.toByteArray(), EncodingEnum.DEFAULT_ENCODING.getEncoding());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("无效编码，" + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String sign(Map<String, String> params) throws RuntimeException {
        if (params == null || params.isEmpty()) {
            throw new RuntimeException("签名参数不能为空");
        }
        String paramStr = concatParams(params);
        String digest = md5Digest(paramStr);

        try {
            Signature signer = Signature.getInstance(SIGNATURE_ALGORITHM);
            signer.initSign(this.privateKey);
            signer.update(digest.getBytes(EncodingEnum.DEFAULT_ENCODING.getEncoding()));
            return Base64.getEncoder().encodeToString(signer.sign());
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException
                | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkSign(Map<String, String> params, String sign) throws RuntimeException {
        if (params == null || params.isEmpty()) {
            throw new RuntimeException("验签参数不能为空");
        }
        if (params.containsKey(ParamEnum.SIGN.getParamName())) {
            params.remove("sign");
        }
        String paramStr = concatParams(params);
        String digest = md5Digest(paramStr);
        try {
            Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifier.initVerify(publicKey);
            verifier.update(digest.getBytes(EncodingEnum.DEFAULT_ENCODING.getEncoding()));
            return verifier.verify(Base64.getDecoder().decode(sign));
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException |
                UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据私钥路径初始化私钥
     *
     * @param privateKeyPath
     * @throws RuntimeException
     */
    private void initPrivateKey(String privateKeyPath) throws RuntimeException {
        File privateKeyFile = new File(privateKeyPath);
        initPrivateKey(privateKeyFile);
    }

    /**
     * 根据私钥文件初始化私钥
     *
     * @param privateKey
     * @throws RuntimeException
     */
    private void initPrivateKey(File privateKey) throws RuntimeException {
        try {
            byte[] privateKeyBytes = Files.readAllBytes(Paths.get(privateKey.getPath()));
            initPrivateKey(privateKeyBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据私钥字节码初始化私钥
     *
     * @param privateKeyBytes
     * @throws RuntimeException
     */
    private void initPrivateKey(byte[] privateKeyBytes) throws RuntimeException {
        try {
            String privateKeyString = new String(privateKeyBytes, EncodingEnum.DEFAULT_ENCODING.getEncoding());
            privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "");
            privateKeyString = privateKeyString.replace("-----END PRIVATE KEY-----", "");
            privateKeyString = privateKeyString.replaceAll("\\s", "");

            byte[] privateKeyRaw = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyRaw);
            KeyFactory privateKeyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = privateKeyFactory.generatePrivate(keySpec);

            this.privateKey = privateKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据公钥路径加载公钥
     *
     * @param publicKeyPath
     * @throws RuntimeException
     */
    private void initPublicKey(String publicKeyPath) throws RuntimeException {
        File publicKeyFile = new File(publicKeyPath);
        initPublicKey(publicKeyFile);
    }

    /**
     * 根据公钥文件加载公钥
     *
     * @param publicKey
     * @throws RuntimeException
     */
    private void initPublicKey(File publicKey) throws RuntimeException {
        try {
            byte[] publicKeyBytes = Files.readAllBytes(Paths.get(publicKey.getPath()));
            initPublicKey(publicKeyBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据公钥字节码加载公钥
     *
     * @param pubKeyBytes
     * @throws RuntimeException
     */
    private void initPublicKey(byte[] pubKeyBytes) throws RuntimeException {
        try {
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pubKeyBytes);
            KeyFactory publicKeyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = publicKeyFactory.generatePublic(publicKeySpec);
            this.publicKey = publicKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将参数以key=value&key2=value2的形式凭借
     *
     * @param params 参数
     * @return 拼接后的参数字符串
     */
    private String concatParams(Map<String, String> params) {
        TreeSet<String> keys = new TreeSet<>();
        keys.addAll(params.keySet());
        String paramStr = null;
        for (String s : keys) {
            if (paramStr == null) {
                paramStr = "";
                paramStr += s + "=" + params.get(s);
            } else {
                paramStr += "&" + s + "=" + params.get(s);
            }
        }
        return paramStr;
    }

    /**
     * md5摘要方法
     *
     * @param txt 需要处理的文本
     * @return 摘要信息
     */
    private String md5Digest(String txt) {
        return DigestUtils.md5Hex(txt);
    }
}
