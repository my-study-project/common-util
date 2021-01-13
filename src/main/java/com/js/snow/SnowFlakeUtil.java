package com.js.snow;


import com.js.net.NetUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnowFlakeUtil {
    private static volatile SnowFlake instance;

    /**
     * 数据中心ID
     **/
    private static volatile long datacenterId;

    /**
     * 机器ID
     **/
    private static volatile long machineId;

    public static SnowFlake getInstance() {
        if (instance == null) {
            synchronized (SnowFlake.class) {
                if (instance == null) {
                    initManyId();
                    log.info("获取雪花算法工具包为空，开始初始化雪花算法工具包数据中心id={},机器id={}", datacenterId, machineId);
                    instance = new SnowFlake(machineId, datacenterId);
                }
            }
        }
        return instance;
    }

    private static void initManyId() {
        String ip = NetUtils.getHostIp();
        log.info("当前环境的ip为{}",ip);
        datacenterId = Long.valueOf(NetUtils.getThreeIp(ip));
        machineId = Long.valueOf(NetUtils.getLastIp(ip));
    }
}