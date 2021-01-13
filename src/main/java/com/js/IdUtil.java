package com.js;

import com.js.snow.SnowFlakeUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author dujie 分布式id生成器
 */
public class IdUtil {

    @Autowired
    private static SnowFlakeUtil snowFlakeUtil;

    private IdUtil() {
        throw new IllegalStateException("IdUtils工具异常");
    }

    public static synchronized String get32Uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static synchronized String getUuid() {

        return String.valueOf(SnowFlakeUtil.getInstance().nextId()).replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(1 << 10);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1 << 10; i++) {
            String id = getUuid();
            list.add(id);
//            System.out.println(id);
        }
        System.out.println(list.stream().distinct().count());
    }
}
