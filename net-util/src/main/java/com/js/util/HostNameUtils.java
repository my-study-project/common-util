package com.js.util;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HostNameUtils {

    private static final String PATTERN_HOSTNAME = "(?<=//|)((\\w)+\\.)+\\w+";

    /**
     * @Description: /获取域名前缀
     * @Date: 3:31 PM 2020/11/30
     * @Param: [url]
     * @return: java.lang.String
     **/
    public static String getHostName(String url) {
        String treaty = "";
        try {
            URL address = new URL(url);
            URI uri = address.toURI();
            treaty = uri.getScheme();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String host = "";
        if (StringUtils.isBlank(url)) {
            return host;
        }

        Pattern p = Pattern.compile(PATTERN_HOSTNAME);
        Matcher matcher = p.matcher(url);
        if ((matcher.find())) {
            host = matcher.group();
        }
        return treaty + "://" + host;
    }

    /**
     * @Description: 删除域名前缀
     * @Date: 3:31 PM 2020/11/30
     * @Param: [url]
     * @return: java.lang.String
     **/
    public static String deleteHostName(String url) {
        String resultUrl = "";
        if (StringUtils.isBlank(url)) {
            return resultUrl;
        }
        return url.replace(getHostName(url), "");
    }

    /**
     * @Description: 添加域名前缀
     * @Date: 3:31 PM 2020/11/30
     * @Param: [domain, url]
     * @return: java.lang.String
     **/
    public static String addHostName(String domain, String url) {
        return domain + url;
    }

    public static void main(String[] args) {
        final String hostName = getHostName("https://www.baidu.com");
        System.out.println(hostName);
    }
}