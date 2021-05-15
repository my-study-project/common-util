package com.js.util;

import com.alibaba.excel.EasyExcel;
import com.js.listener.ExcelListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

public class EasyExcleUtil {
    private static final String USER_AGENT = "User-Agent";

    private static final String MSIE = "MSIE";

    private static final String TRIDENT = "Trident";

    private static final String UTF_8 = "UTF-8";

    private static final String ISO_8859_1 = "ISO-8859-1";

    private static final String XLSX = ".xlsx";

    /**
     * @return
     * @Description: 导出文件 适合简单的list类型
     * @Param [request, response, filenames, list, clazz]
     * @Author: 渡劫 dujie
     * @Date: 2021/5/14 1:49 PM
     */
    public static <T> void exportExcle(HttpServletRequest request, HttpServletResponse response, String filenames, List<T> list, Class clazz) throws Exception {
        String userAgent = request.getHeader(USER_AGENT);
        if (userAgent.contains(MSIE) || userAgent.contains(TRIDENT)) {
            filenames = URLEncoder.encode(filenames, UTF_8);
        } else {
            filenames = new String(filenames.getBytes(UTF_8), ISO_8859_1);
        }
        response.setContentType("application/vnd.ms-exce");
        response.setCharacterEncoding("utf-8");
        response.addHeader("Content-Disposition", "filename=" + filenames + XLSX);
        EasyExcel.write(response.getOutputStream(), clazz).sheet("sheet").doWrite(list);
    }

    /**
     * @return
     * @Description: 导入文件解析
     * @Param [multipartFile, clazz, excelListener]
     * @Author: 渡劫 dujie
     * @Date: 2021/5/14 1:50 PM
     */
    public static <T extends ExcelListener> void importExcle(File multipartFile, Class clazz, T listener) throws Exception {
        InputStream inputStream = new FileInputStream(multipartFile);
        //传入参数
        EasyExcel.read(inputStream, clazz, listener).sheet().doRead();
    }

    /**
     * @return
     * @Description: 导出想要的列
     * @Param [notNeedCol 需要排除的列, fileName, clazz, list]
     * @Author: 渡劫 dujie
     * @Date: 2021/5/14 3:21 PM
     */
    public static <T> void exportNeedCol(Set<String> notNeedCol, String fileName, Class clazz, List<T> list) {
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, clazz).excludeColumnFiledNames(notNeedCol).sheet("模板")
                .doWrite(list);
    }

    /**
     * @return
     * @Description: 导出需要的列
     * @Param [needCol 需要的列, fileName, clazz, list]
     * @Author: 渡劫 dujie
     * @Date: 2021/5/14 3:21 PM
     */
    public static <T> void exportOnlyNeedCol(Set<String> needCol, String fileName, Class clazz, List<T> list) {
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, clazz).includeColumnFiledNames(needCol).sheet("模板")
                .doWrite(list);
    }
}