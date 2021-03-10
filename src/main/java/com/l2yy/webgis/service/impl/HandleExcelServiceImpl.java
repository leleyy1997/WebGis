package com.l2yy.webgis.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.l2yy.webgis.error_code.CommonBizCodeEnum;
import com.l2yy.webgis.exception.BizException;
import com.l2yy.webgis.service.IHandleExcelService;
import com.l2yy.webgis.util.ExcelUtil;
import com.l2yy.webgis.util.Toolkit;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class HandleExcelServiceImpl<T> implements IHandleExcelService<T> {
    @Override
    public void export(String title, LinkedHashMap<String, String> headMap, List<T> list, HttpServletResponse response) throws BizException {
        if (Toolkit.isNull(title, headMap, response, list, response)) {
            throw new BizException(CommonBizCodeEnum.GET_DATA_IS_EMPTY);
        }
        JSONArray data = new JSONArray((List<Object>) list);
        try {
            ExcelUtil.downloadExcelFile(title, headMap, data, response);
        } catch (IOException e) {
            throw new BizException(CommonBizCodeEnum.EXCEL_EXPORT_ERROR);
        }
    }

    @Override
    public void exportTemplate(String title, String[] headStrings, Map<String, String> annoMap, HttpServletResponse response) throws BizException {
        if (Toolkit.isNull(title, headStrings, response)) {
            throw new BizException(CommonBizCodeEnum.GET_DATA_IS_EMPTY);
        }
        ExcelUtil.downloadExcelTemplateFile(title, headStrings, annoMap, response);
    }
}

