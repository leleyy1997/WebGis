package com.l2yy.webgis.service;

import com.l2yy.webgis.exception.BizException;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ncjdjyh
 * @FirstInitial: 2019/4/9
 * @Description: ~
 */
public interface IHandleExcelService<T> {
    /**
     * @Param:
     * @Author: ncjdjyh
     * @Description: excel 导出
     */
    void export(String title,
                LinkedHashMap<String, String> headMap,
                List<T> data,
                HttpServletResponse response) throws BizException;

    /**
     * @Param: title 表名称
     * @Param: headStrings: 模板表的表头 例如 String[] s = new String[]{"客户姓名", "手机号", "黑名单来源", "黑名单类型", "黑名单时长", "黑名单状态"};
     * @Author: ncjdjyh
     * @Description: excel 导出模板
     */
    void exportTemplate(String title, String[] headStrings, Map<String, String> annoMap, HttpServletResponse response) throws BizException;

}
