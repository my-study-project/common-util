package com.js.snow;


import com.js.net.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;

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


    private static SnowFlake snowFlake;

    static {
        Long workerId = getWorkId();
        Long datacenterId = getDatacenterId();
        snowFlake = new SnowFlake(datacenterId, workerId);
    }
    /**
     * 生成雪花算法ID
     * generateId
     * @return
     */
    public static long genId() {
        return snowFlake.nextId();
    }

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

    private static Long getWorkId(){
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for(int b : ints){
                sums += b;
            }
            return (long)(sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0,31);
        }
    }

    private static Long getDatacenterId(){
        int[] ints = StringUtils.toCodePoints(SystemUtils.getHostName());
        int sums = 0;
        for (int i: ints) {
            sums += i;
        }
        return (long)(sums % 32);
    }
}