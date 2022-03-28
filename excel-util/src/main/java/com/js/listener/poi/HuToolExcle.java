package com.js.util.poi;

import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import io.swagger.annotations.ApiModelProperty;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HuToolExcle {

    static final Log logger = LogFactory.get(HuToolExcle.class);

    /**
     * @return void
     * @Description: 依赖hutool工具类的excle导出
     * @Param [response, clazz, list]
     * @Author: 渡劫 dujie
     * @Date: 5/15/21 11:26 AM
     */
    public static void export(HttpServletResponse response, Class clazz, List<?> list) throws Exception {
        if (!list.isEmpty()) {
            if (!list.get(0).getClass().equals(clazz)) {
                logger.error("数据类型与传入的集合数据类型不一致！数据类型：{}; 集合数据类型：{}", clazz, list.get(0).getClass());
                throw new Exception("数据类型与传入的集合数据类型不一致！");
            } else {
                ExcelWriter writer = ExcelUtil.getWriter();
                // 获取当前类字段
                Field[] fields = clazz.getDeclaredFields();
                // 字段名称集合
                List<String> fieldNames = new ArrayList<>();
                // 字段中文名称集合（获取实体中@ApiModelProperty注解value的值）
                List<String> cnNames = new ArrayList<>();
                for (Field field : fields) {
                    if (!field.isAccessible()) {
                        // 关闭反射访问安全检查，为了提高速度
                        field.setAccessible(true);
                    }
                    String fieldName = field.getName();
                    // 排除ID和序号
                    if (!"sid".equals(fieldName) && !"serialVersionUID".equals(fieldName) && !"ordernum".equals(fieldName)) {
                        fieldNames.add(fieldName);
                    }
                    // 判断是否有@ApiModelProperty注解
                    boolean annotationPresent = field.isAnnotationPresent(ApiModelProperty.class);
                    if (annotationPresent && !"sid".equals(fieldName)) {
                        ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                        String name = annotation.value();
                        cnNames.add(name);
                    }
                }
                String[] fs = fieldNames.toArray(new String[0]);
                String[] ns = cnNames.toArray(new String[0]);
                for (int i = 0; i < ns.length; i++) {
                    // 设置表头及字段名
                    writer.addHeaderAlias(fs[i], ns[i]);
                }
                // 自动换行
                Workbook workbook = writer.getWorkbook();
                StyleSet styleSet = new StyleSet(workbook);
                styleSet.setWrapText();
                writer.setStyleSet(styleSet);
                writer.write(list, true);
                ServletOutputStream out = response.getOutputStream();
                ;
                try {
                    for (int i = 0; i < fieldNames.size(); i++) {
                        writer.setColumnWidth(i, 23);
                    }
                    response.setContentType("application/x-msdownload;charset=utf-8");
                    String ecodeFileName = URLEncoder.encode("excel", "UTF-8");
                    response.setHeader("Content-Disposition", "attachment;filename=" + ecodeFileName + ".xls");
                    writer.flush(out, true);
                    writer.close();
                    IoUtil.close(out);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            logger.error("数据集合为空");
            throw new Exception("数据集合为空");
        }
    }
}
