package com.js.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.js.listener.AllExcelListener;
import com.js.listener.ExcelListener;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ALL")
public class EasyExcleUtil {

    private static EasyExcleUtil instance;

    private EasyExcleUtil() {

    }

    public static EasyExcleUtil getInstance() {
        if (null == instance) {
            synchronized (EasyExcleUtil.class) {
                if (null == instance) {
                    instance = new EasyExcleUtil();
                }
            }
        }
        return instance;
    }

    private static final String USER_AGENT = "User-Agent";

    private static final String MSIE = "MSIE";

    private static final String TRIDENT = "Trident";

    private static final String UTF_8 = "UTF-8";

    private static final String ISO_8859_1 = "ISO-8859-1";

    private static final String XLSX = ".xlsx";

    private static final String CONTENT_TYPE = "application/vnd.ms-excel";

    private static final String UTF8 = "utf8";

    private static final String HEAD_CONTENT = "Content-Disposition";

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
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8);
        response.addHeader(HEAD_CONTENT, "filename=" + filenames + XLSX);
        EasyExcel.write(response.getOutputStream(), clazz).sheet("sheet").doWrite(list);
    }

    /**
     * @return
     * @Description: 导入文件解析 默认分组处理 incoke方法调用的 doSomething方法
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
     * @Description: 导入文件解析 默认不分组处理，使用者统一实现 doSomething()方法可以自己去重和分组 在after方法进行的调用
     * @Param [multipartFile, clazz, excelListener]
     * @Author: 渡劫 dujie
     * @Date: 2021/5/14 1:50 PM
     */
    public static <T extends AllExcelListener> void importExcleNoSplit(File multipartFile, Class clazz, T listener) throws Exception {
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


    /**
     * 导出 Excel ：一个 sheet，带表头.只有一个sheet 并以response流输出
     *
     * @param response  HttpServletResponse
     * @param data      数据 list，每个元素为一个 BaseRowModel
     * @param fileName  导出的文件名
     * @param sheetName 导入文件的 sheet 名
     * @param model     映射实体类，Excel 模型
     * @throws Exception 异常
     */
    public static <T> void writeExcelByResponse(HttpServletResponse response, List<T> data,
                                                String fileName, String sheetName, Class model) throws Exception {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(getOutputStream(fileName, response), model)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet(sheetName)
                .registerWriteHandler(horizontalCellStyleStrategy)
                //最大长度自适应 目前没有对应算法优化 建议注释不用 会出bug
//                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(data);

    }


    /**
     * 导出 Excel ：一个 sheet，带表头. 输出流 简单的写入流
     *
     * @param outputStream OutputStream
     * @param data         数据 list，每个元素为一个 BaseRowModel
     * @param sheetName    导入文件的 sheet 名
     * @param model        映射实体类，Excel 模型
     * @throws Exception 异常
     */
    public static <T> void writeExcelIn(OutputStream outputStream, List<T> data,
                                        String sheetName, Class model) throws Exception {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(outputStream, model)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet(sheetName)
                .registerWriteHandler(horizontalCellStyleStrategy)
                //最大长度自适应 目前没有对应算法优化 建议注释不用 会出bug
//                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(data);

    }


    /**
     * 重复写入多个sheetNo 并导出
     *
     * @param outputStream 输出流
     * @param data         数据源list
     * @param sheetName    sheet名
     * @param model        导出模板类
     * @param sheetNo      想要导出多少个sheet
     * @param <T>
     * @throws Exception
     */
    public static <T> void repeatedWrite(OutputStream outputStream, List<T> data,
                                         String sheetName, Class model, Integer sheetNo) {
        ExcelWriter excelWriter = null;
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        try {
            // 这里 指定文件 写入输出流
            excelWriter = EasyExcel.write(outputStream, model).
                    registerWriteHandler(horizontalCellStyleStrategy).build();
            // 去调用写入,这里传入sheetNo 表示循环多少次写多少个sheet
            for (int i = 0; i < sheetNo; i++) {
                // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样。这里注意DemoData.class 可以每次都变，我这里为了方便 所以用的同一个class 实际上可以一直变
                WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName + i).head(model).build();
                // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                excelWriter.write(data, writeSheet);
            }
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }


    /**
     * 重复写入多个sheetNo 并导出
     *
     * @param outputStream 输出流
     * @param datas        数据源list
     * @param model        导出模板类
     * @param <T>
     * @throws Exception
     */
    public static <T> void writeSheetByData(OutputStream outputStream, Class model, List<T>... datas) {
        ExcelWriter excelWriter = null;
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        try {
            // 这里 指定文件 写入输出流
            excelWriter = EasyExcel.write(outputStream, model).
                    registerWriteHandler(horizontalCellStyleStrategy).build();
            // 去调用写入,这里传入sheetNo 表示循环多少次写多少个sheet
            int i = 0;
            for (List<T> data : datas) {
                // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样。这里注意DemoData.class 可以每次都变，我这里为了方便 所以用的同一个class 实际上可以一直变
                WriteSheet writeSheet = EasyExcel.writerSheet(i, String.valueOf(i)).head(model).build();
                // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                excelWriter.write(data, writeSheet);
                i++;
            }
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }


    /**
     * 导出 Excel ：一个 sheet，带表头.模板写入指定sheet并导出 不用浏览器的outputStream
     * 指定写入到哪个sheet中
     *
     * @param outputStream OutputStream 输出流
     * @param data         数据 list，每个元素为一个 BaseRowModel
     * @param in           输入流  模板
     * @param sheetNo      导入文件的 sheet  指定输出在哪一个sheet中，角标从0开始
     * @param model        映射实体类，Excel 模型
     */
    public static <T> void writeExcelInSheetNo(OutputStream outputStream, List<T> data,
                                               InputStream in, String sheetName, Class model, Integer sheetNo) {
        ExcelWriter excelWriter;
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo).sheetName(sheetName).build();
        //填写要写入的流文件  Excel文件
        excelWriter = EasyExcel.write(outputStream, model).withTemplate(in)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .build();
        // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
        excelWriter.write(data, writeSheet);
        excelWriter.finish();
    }


    /**
     * 导出文件时为Writer生成OutputStream.
     *
     * @param fileName 文件名
     * @param response response
     * @return 响应流输出
     * @throws Exception exception
     */
    private static OutputStream getOutputStream(String fileName,
                                                HttpServletResponse response) throws Exception {
        try {
            fileName = URLEncoder.encode(fileName, UTF_8);
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding(UTF8);
            response.setHeader(HEAD_CONTENT, "attachment; filename=" + fileName + XLSX);
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            return response.getOutputStream();
        } catch (IOException e) {
            throw new Exception("导出excel表格失败!", e);
        }
    }


}