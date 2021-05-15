package com.js.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * EasyExcel 导入监听
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
@Slf4j
public abstract class ExcelListener<T> extends AnalysisEventListener<T> {
    // 可以通过实例获取该值
    private List<T> datas = new ArrayList<>();

    public List<T> getDatas() {
        return this.datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        //数据存储到list，供批量处理，或后续自己业务逻辑处理。
        this.datas.add(data);
        log.info("数据域范围内{}", this.datas);
        //根据自己业务做处理
        doSomething(datas);
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //解析结束销毁不用的资源
        datas.clear();
    }

    /**
     * @return void
     * @Description: 需要重写的方法
     * @Param [object]
     * @Author: 渡劫 dujie
     * @Date: 5/14/21 9:26 PM
     */
    public abstract void doSomething(List<T> object);
}