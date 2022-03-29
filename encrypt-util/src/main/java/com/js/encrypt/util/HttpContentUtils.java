package com.js.encrypt.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class HttpContentUtils {
    /**
     * 报文加密
     * key	对称加密随机密钥，解密data字段用
     * data	对称加密后的业务数据，内容由具体接口确定
     * sign 签名
     *
     * @param data
     * @param publicKey  对方提供的RSA公钥
     * @param privateKey 当前系统RSA私钥
     * @return
     * @throws Exception
     */
    public static String generateData(String data, String publicKey, String privateKey) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        // 生成AES key
        String aesKey = AESUtils.generateAESKey();
        // 使用AES KEY 加密data
        String encryptData = AESUtils.encrypt(data, aesKey);
        params.put("data", encryptData);
        // 使用对方RSA公钥加密aesKey
        String encryptAESKey = RSAUtils.encryptByPublicKey(aesKey, publicKey);
        params.put("key", encryptAESKey);
        // 使用当前系统RSA私钥加签data
        String sign = RSAUtils.signByPrivateKey(data, privateKey);
        params.put("sign", sign);
        return JSON.toJSONString(params);
    }

    /**
     * 解析报文
     *
     * @param jsonStr
     * @param privateKey 当前系统RSA私钥
     * @param publicKey  对方RSA公钥
     * @return
     * @throws Exception
     */
    public static String parseData(String jsonStr, String privateKey, String publicKey) throws Exception {
        Map<String, Object> map = JSON.parseObject(jsonStr);
        String data = (String) map.get("data");
        String sign = (String) map.get("sign");
        String key = (String) map.get("key");
        // 使用当前系统RSA私钥解密aesKey
        String aesKey = RSAUtils.decryptByPrivateKey(key, privateKey);
        // 使用AES KEY 解密data数据
        String decryptData = AESUtils.decrypt(data, aesKey);
        // 使用对方RSA公钥验签
        if (!RSAUtils.verifySignByPublicKey(decryptData, sign, publicKey)) {
            throw new RuntimeException("验签未通过");
        }
        return decryptData;
    }

    /**
     * 使用工具 生成公私钥.bat 双方各生成一对公私钥，并将公钥提供给对方
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //1.渠道加密请求报文
        //公钥
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJ7RR23Sq6Z/g6RhAa6o51XTkWXBh8yNFhlksZZoukscqhMHEzBUPwY6BrJJFhnDAzistH3D3L8GJMRjyeCVk0qgnm8iI+6kfolHARZ+IZhDLk3P77hPMETYF5ioGgWByc7M19M0a3ARsKJ5+bik1x57e0yyPxxCOj0BXZUpHEhwIDAQAB";
        //渠道私钥
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANixJTBMFKuzToLjFHOifen3Rv+1wbQ/I5pwk4+wUKIWkxLJjhKwXIHLzPkeTBSbkva4xlzyUl3FS4U9x6Nixywe1zxA0RJosNgu3VilAvXSEEKMJw1ZaMsWJixuNCtTQmkZXKrDQKWTlUSXvWMFZaAWAuNwBbDoEraBAmyPkKpJAgMBAAECgYADzWf3pv4d3TGdflqXhNxsxJgMWKYjgZAgX6VXOiLQFVFTXRIPJeUZqffYzz1Kh0hW5/sjGmVzm8itd94O8ebIySVmZ7yknC/RWwmBzSgVwVIylMiC2xQcCCchdQM3RacpEl1b+wykml5+rOt3pfOli/YuBk9PgcTYUMPbTxSM+QJBAO92Wib8XCaRIvP2ZdVM6ojuDF+Vzv8pn0cHDlED18sQRD2nCbG7us1XX7Y3ovyXWvuo5Wazu2HaT6A4j1Yg33MCQQDnqDfGV4dTZF+O2y+ojql4VlLlFuOemGmiyUcKyPj1n4GRtRtpiI5jmrZDcEDU30zCQmYbuNhYSNsqqALb+OhTAkEAia5BvWKh1SbjsuN7v30+6EX+ZULsRjd3Nc5vw9Ly0vViVxRgdYlbqU7QYCNzGcoobMO6SieS2Hs+BwAeIcY60wJANaPO3l9QF3ArbEG2hFocRZ//a8pkBEoYU7g1efrVItK/RsTfxwz2o3ukbnMkn+XguS50xECF39Nm5DapCtRUvwJAD/OhsalOlosHMJWaVERXGxtVz7KES9pS6olPzkjuR5h93YmNbq5WTMLNsAbAW0wWW2QNY3+WB85lM8t7oDW+3Q==";
        String requestData = "{\"header\":{\"timestamp\":\"20200527145026\",\"requestNo\":\"1560278345908097\"},\"body\":{\"creditAuthorization\":\"1\",\"certType\":\"1\",\"bizType\":\"ADMIT_APPLY\",\"occupation\":\"9902\",\"address\":\"山东省莒县\",\"repayMode\":\"2\",\"certValidEndDate\":\"20360920\",\"reserve1\":\"其他类职业\",\"channelType\":\"1\",\"source\":\"USER\",\"mobileNo\":\"15288800001\",\"certNo\":\"371122198911999999\",\"adviceCreditLimit\":\"2000.00\",\"applyNo\":\"0141518376731590777221300\",\"name\":\"赵国\",\"creditAppDate\":\"2020-05-27\",\"channelCode\":\"2018\"}}";
        String encryRequestData = generateData(requestData, publicKey, privateKey);
        System.out.println("encryRequestData:" + encryRequestData);
        //私钥
        String privateKey_ = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMntFHbdKrpn+DpGEBrqjnVdORZcGHzI0WGWSxlmi6SxyqEwcTMFQ/BjoGskkWGcMDOKy0fcPcvwYkxGPJ4JWTSqCebyIj7qR+iUcBFn4hmEMuTc/vuE8wRNgXmKgaBYHJzszX0zRrcBGwonn5uKTXHnt7TLI/HEI6PQFdlSkcSHAgMBAAECgYAnk6UYKv8N7ATV/xd2/xQO4semn0RZaM7JDrkHcHbcP9CUSTdWpaiMPCqb9V3dQMydvY1cN0mRaDsL8hQ9cV2fQLFUda27mf9Ot9+nxuRYiz5/b44G2hlZUyFC+MNTpjoRuOoLgZIwu4emqVAQ13Gd1rAjbvW8jUOpYJhDf8yU4QJBAOkk3YtkyBb0702hEHUi8IOIYxNhNyfA+S2SOt7DoNczpjMqf9GR+R9WlJptAyKksCKyAL/HMK0DVO3OTl7/mJECQQDduL/W+00Y+yXvm5JOZuvyCWE9rdKNOP2VMMrWbRggPEq10JTB1YpUKFgdoqNqgAO2MeKyylGfSIOS03L0XdeXAkEAihp5VYn0cIzUeDKRUiHJQND4h1FN37GSqOj3EF/nlbKVn7dsEZMbc/HQgw1SDyJo55RzlyaB8eqIU3miuOlE0QJAbbV41ztJjs7WmpTz8+9hWEXj480and5RO30FIMyiX3D11rj6ol6zL6k20JMlIRFuxLwyXB1X/eS3lfMisl+syQJBAJM7ZyVD51E68k1bH2T1t65Ry8sAI8DiEIxvcERp/m6B/Kzgjv0AfyixAv2Ert76JhEXP4bjngh5AHxoXtPbzxg=";
        //渠道公钥
        String publicKey_ = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDYsSUwTBSrs06C4xRzon3p90b/tcG0PyOacJOPsFCiFpMSyY4SsFyBy8z5HkwUm5L2uMZc8lJdxUuFPcejYscsHtc8QNESaLDYLt1YpQL10hBCjCcNWWjLFiYsbjQrU0JpGVyqw0Clk5VEl71jBWWgFgLjcAWw6BK2gQJsj5CqSQIDAQAB";
        String decryRequestData = parseData(encryRequestData, privateKey_, publicKey_);
        System.out.println("decryRequestData:" + decryRequestData);
        //响应报文
        String responseData = "{\"header\":{\"timetamp\":\"20200527145026\",\"requestNo\":\"1560278345908097\",\"code\":\"000000\",\"message\":\"访问成功\"},body:{\"applyNo\":\"014869090621590562139833\",\"resultCode\":\"0000\",\"reserve1\":\"\",\"reserve3\":\"\",\"reserve2\":\"\",\"retry\":\"N\",\"resultMsg\":\"成功\",\"extInfo\":\"\"}}";
        String encryResponseData = generateData(responseData, publicKey_, privateKey_);
        System.out.println("encryResponseData:" + encryResponseData);
        //4.渠道解析响应报文
        String decryResponseData = parseData(encryResponseData, privateKey, publicKey);
        System.out.println("decryResponseData:" + decryResponseData);

    }
}
