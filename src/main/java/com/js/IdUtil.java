package com.js;

import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.UUID;

/**
 * @Author dujie 分布式id生成器
 */
public class IdUtil {

    private static HashMap<String,SnowFlake> snowFlakeHashMap = new HashMap<>();

    private IdUtil() {
        throw new IllegalStateException("IdUtils工具异常");
    }

    public static synchronized String get32Uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static synchronized String getUuid() {
        SnowFlake snowFlake = snowFlakeHashMap.get("snowFlake");
        if (ObjectUtils.isEmpty(snowFlake)){
            snowFlake = new SnowFlake(2, 3);
            snowFlakeHashMap.put("snowFlake",snowFlake);
        }
        return String.valueOf(snowFlake.nextId()).replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(1 << 10);
        for (int i = 0; i < 1 << 10; i++) {
            System.out.println(getUuid());
        }
        System.out.println("test1");
    }
}
