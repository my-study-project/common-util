package com.js.net;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author 渡劫 dujie
 * @date 2020/7/20 19:36
 **/
@Slf4j
public class NetUtils {

    /**
     * 获取本机本地IP
     */
    static void getLocalIP(){
        InetAddress ia=null;
        try {
            ia= InetAddress.getLocalHost();
            String localname=ia.getHostName();
            String localip=ia.getHostAddress();
            System.out.println("本机名称是："+ localname);
            System.out.println("本机的ip是 ："+localip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取本地真正的IP地址，即获得有线或者 无线WiFi 地址。
     * 过滤虚拟机、蓝牙等地址
     *
     * @return IPv4
     */
    public static String getRealIP() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                // 去除回环接口，子接口，未运行和接口
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }

                if (!netInterface.getDisplayName().contains("Intel")
                        && !netInterface.getDisplayName().contains("Realtek")
                        && !netInterface.getDisplayName().contains("Ethernet")) {
                    continue;
                }

                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null) {
                        // ipv4
                        if (ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
                break;
            }
        } catch (SocketException e) {
            //捕获异常
        }
        return null;
    }

    /**
     * @Description: 获取主机ip
     * @Return: java.lang.String
     * @Author: 渡劫 dujie
     * @Date: 2021/1/2 11:16 PM
     **/
    public static String getHostIp(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param ip
     * @Description: 获取ip的末尾内容
     * @Return: java.lang.String
     * @Author: 渡劫 dujie
     * @Date: 2021/1/2 11:31 PM
     **/
    public static String getLastIp(String ip) {
        if (StringUtils.isNotBlank(ip)) {
            String[] ipList = StringUtils.split(ip, ".");
            return ipList[ipList.length - 1];
        }
        return "1";
    }

    /**
     * @param ip
     * @Description: 获取ip的第三组数据
     * @Return: java.lang.String
     * @Author: 渡劫 dujie
     * @Date: 2021/1/4 12:49 PM
     **/
    public static String getThreeIp(String ip) {
        if (StringUtils.isNotBlank(ip)) {
            String[] ipList = StringUtils.split(ip, ".");
            return ipList[ipList.length - 2];
        }
        return "1";
    }
}
