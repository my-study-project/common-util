package com.js.util.sm;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;

import java.security.KeyPair;

public class SmUtils {
//    https://www.bookstack.cn/read/hutool/3409b7f1fbc6e74e.md#%E9%9D%9E%E5%AF%B9%E7%A7%B0%E5%8A%A0%E5%AF%86SM2
//    https://www.javajike.com/book/hutool/chapter8/eb920b20c199717f34f28a89fcf6d620.html
    public String encode(){
        String text ="我是一段测试aaaa";
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();
        SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
        // 公钥加密，私钥解密
        String encryptStr = sm2.encryptBcd(text, KeyType.PublicKey);
        String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr,KeyType.PrivateKey));
        return null;
    }
}
