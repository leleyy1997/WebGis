package com.l2yy.webgis.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.l2yy.webgis.error_code.CommonBizCodeEnum;
import com.l2yy.webgis.exception.BizException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @Author: ncjdjyh
 * @FirstInitial: 2019/4/8
 * @Description: ~
 */
public class ExcelUtil<T> {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    public static String DEFAULT_DATE_PATTERN = "yyyy年MM月dd日";
    public static int DEFAULT_COLUMN_WIDTH = 17;
    // 默认数据开始行
    public static int DEFAULT_DATA_INDEX = 2;

    /**
     * 导出Excel 2007 (.xlsx)格式
     *
     * @param title       标题行
     * @param headMap     属性-列头
     * @param data        数据集
     * @param datePattern 日期格式，传null值则默认 年月日
     * @param colWidth    列宽 默认 17个字节
     * @param out         输出流
     */
    public static void exportExcel(String title, LinkedHashMap<String, String> headMap,
                                   JSONArray data, String datePattern,
                                   int colWidth, OutputStream out) throws BizException {
        exportExcel(title, headMap, data, datePattern, null, colWidth, out);
    }

    public static void exportExcel(String title, LinkedHashMap<String, String> headMap,
                                   JSONArray data, String datePattern, Map<String, String> annoMap,
                                   int colWidth, OutputStream out) throws BizException {
        if (datePattern == null) {
            datePattern = DEFAULT_DATE_PATTERN;
        }
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet();
        int minBytes = colWidth < DEFAULT_COLUMN_WIDTH ? DEFAULT_COLUMN_WIDTH : colWidth;
        int[] arrColWidth = new int[headMap.size()];
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter.hasNext();) {
            String fieldName = iter.next();
            properties[ii] = fieldName;
            headers[ii] = headMap.get(fieldName);
            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] = bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }
        CellStyle cellStyle = createCellStyle(workbook);
        createHeader(title, headers, annoMap, workbook, cellStyle, sheet);
        // 遍历集合数据，产生数据行
        int rowIndex = DEFAULT_DATA_INDEX;
        for (Object obj : data) {
            JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
            Row dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                Cell newCell = dataRow.createCell(i);
//                Object o = jo.get(properties[i]);
                Object o = getValueByProperty(properties[i],jo);
                String propertiesValue = getPropertiesValue(datePattern, o);
                newCell.setCellValue(propertiesValue);
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        try {
            workbook.write(out);
            workbook.dispose();
        } catch (IOException e) {
            logger.error("导出excel异常", e);
            throw new BizException(CommonBizCodeEnum.EXCEL_EXPORT_ERROR);
        }
    }

    //从属性参数获取对象的属性值，支持以【.】获取下属成员变量
    private static Object getValueByProperty(String propertyName, JSONObject dataObject){
        if(propertyName.contains(".")){
            String[] properties = propertyName.split("\\.");
            JSONObject data = dataObject;
            for(String propertyTemp : properties){
                if(data==null){
                    return null;
                }
                Object dataTemp =  getValueByProperty(propertyTemp,data);
                if(dataTemp instanceof JSONObject){
                    data = (JSONObject)dataTemp;
                }else{
                    return dataTemp;
                }
            }
            return data;
        }else{
            return dataObject.get(propertyName);
        }
    }


    public static void exportExcel(String title, LinkedHashMap<String, String> headMap, JSONArray jsonArray, OutputStream out) throws BizException {
        exportExcel(title, headMap, jsonArray, DEFAULT_DATE_PATTERN, DEFAULT_COLUMN_WIDTH, out);
    }

    // Web 导出 excel
    public static void downloadExcelFile(String title, LinkedHashMap<String, String> headMap,
                                         JSONArray data, HttpServletResponse response) throws BizException, IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ExcelUtil.exportExcel(title, headMap, data, os);
        downloadExcel(title, os, response);
    }

    private static void downloadExcel(String title, ByteArrayOutputStream os, HttpServletResponse response) throws BizException {
        try {
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((title + ".xlsx").getBytes(), "iso-8859-1"));
            response.setContentLength(content.length);
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);

            }
            bis.close();
            bos.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            logger.error("导出excel异常", e);
            throw new BizException(CommonBizCodeEnum.EXCEL_EXPORT_ERROR);
        }
    }

    // Web 导出 excel 模板
    public static void downloadExcelTemplateFile(String title, String[] headMap, Map<String, String> annoMap, HttpServletResponse response) throws BizException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ExcelUtil.exportExcelTemplate(title, headMap, os, annoMap);
        downloadExcel(title, os, response);
    }

    public static void exportExcelTemplate(String title, String[] headStrings, OutputStream out) throws BizException {
        exportExcelTemplate(title, headStrings, out, null);
    }

    public static void exportExcelTemplate(String title, String[] headStrings, OutputStream out, Map<String, String> annoMap) throws BizException {
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        workbook.setCompressTempFiles(true);
        // 单元格样式
        CellStyle cellStyle = createCellStyle(workbook);
        Sheet sheet = workbook.createSheet();
        createHeader(title, headStrings, annoMap, workbook, cellStyle, sheet);
        try {
            workbook.write(out);
            workbook.dispose();
        } catch (IOException e) {
            logger.error("导出excel异常", e);
            throw new BizException(CommonBizCodeEnum.EXCEL_EXPORT_ERROR);
        }
    }

    private static void createHeader(String title, String[] headStrings, Map<String, String> annoMap,
                                     SXSSFWorkbook workbook, CellStyle cellStyle, Sheet sheet) {
        int[] arrColWidth = new int[headStrings.length];
        int ii = 0;

        for (String s : headStrings) {
            int bytes = s.getBytes().length;
            arrColWidth[ii] = bytes < DEFAULT_COLUMN_WIDTH ? DEFAULT_COLUMN_WIDTH : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue(title);
        titleRow.getCell(0).setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headStrings.length - 1));
        Row headerRow = sheet.createRow(1);
        Drawing drawing = sheet.createDrawingPatriarch();
        CreationHelper factory = workbook.getCreationHelper();
        for (int i = 0; i < headStrings.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headStrings[i]);
            if (annoMap != null && annoMap.size() > 0) {
                // 首先通过利用注解中的 code 查字典表的形式获取 noticeMessage, 如果 code 不存在, 从 noticeMessage 中取, 如果都不存在, 不添加批注和下拉框
                String headName = headStrings[i];
                String message = annoMap.get(headName);
                if (StringUtils.isEmpty(message)) {
                    String[] downListValues = message.split(",");
                    ClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) i, 1, (short) i + 2, 3);
                    Comment comment = drawing.createCellComment(anchor);
                    RichTextString str = factory.createRichTextString(message);
                    comment.setString(str);
                    cell.setCellComment(comment);
                    addDownLists(sheet, downListValues, i);
                }
            }
        }
    }

    private static CellStyle createCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        Font cellFont = workbook.createFont();
        cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        cellStyle.setFont(cellFont);
        return cellStyle;
    }

    /* 添加下拉框 */
    private static void addDownLists(Sheet sheet, String[] values, int index) {
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        for (int i = DEFAULT_DATA_INDEX; i < 100; i++) {
            DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(values);
            CellRangeAddressList addressList = new CellRangeAddressList(i, i, index, index);
            DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
            if (validation instanceof XSSFDataValidation) {
                validation.setSuppressDropDownArrow(true);
                validation.setShowErrorBox(true);
            } else {
                validation.setSuppressDropDownArrow(false);
            }
            sheet.addValidationData(validation);
        }
    }

//    /**
//     * @Param: indexMap: Excel 表列数与实体字段的对应关系
//     * @Auther: ncjdjyh
//     * @Description: ~
//     */
//    public static <T> List<T> importExcel(InputStream is, Map<Integer, String> indexMap, Class<T> tClass) throws MyException {
//        JSONArray data = new JSONArray();
//        List<T> resultList;
//        Workbook workbook;
//        try {
//            workbook = WorkbookFactory.create(is);
//        } catch (Exception ex) {
//            logger.error("导入excel异常", ex);
//            throw new MyException(CommonBizCodeEnum.UPLOAD_FILE_ERROR.getCode(), CommonBizCodeEnum.UPLOAD_FILE_ERROR.getMsg());
//        }
//            Sheet sheet = workbook.getSheetAt(0);
//            for (int i = DEFAULT_DATA_INDEX; i < sheet.getLastRowNum() + 1; i++) {
//                JSONObject dataMap = new JSONObject();
//                Row row = sheet.getRow(i);
//                if(row==null){
//                    continue;
//                }
//                for (int j = 0; j < row.getLastCellNum(); j++) {
//                    Cell cell = row.getCell(j);
//                    String cv = getCellValue(cell);
//                    String v = indexMap.get(j);
//                    if(Toolkit.isValid(v)){
//                        equipData(dataMap,cv,v);
//                    }
////                    if (Toolkit.isValid(cv)) {
////                        dataMap.put(v, cv);
////                    }
//                }
//                if (!dataMap.isEmpty()) {
//                    data.add(dataMap);
//                }
//            }
//            if (data.isEmpty()) {
//                throw new BizException(CommonBizCodeEnum.FILE_IS_EMPTY.getCode(), CommonBizCodeEnum.FILE_IS_EMPTY.getMessage());
//            }
//            resultList = data.toJavaList(tClass);
//
//        return resultList;
//    }

    private static void equipData(JSONObject jsonObject, String value, String property) {
        if (property.contains(".")) {
            String key = property.substring(0, property.indexOf("."));
            String propertyNext = property.substring(key.length() + 1);
            JSONObject jsonObjectNext;
            if (jsonObject.get(key) == null) {
                jsonObjectNext = new JSONObject();
            } else {
                Object objectTemp = jsonObject.get(key);
                if (objectTemp instanceof JSONObject) {
                    jsonObjectNext = (JSONObject) objectTemp;
                } else {
                    jsonObjectNext = new JSONObject();
                }
            }
            jsonObject.put(key, jsonObjectNext);
            equipData(jsonObjectNext, value, propertyNext);
        } else {
            if (Toolkit.isValid(value)) {
                jsonObject.put(property, value);
            }
        }
    }

    /**
     * 根据Excel表格中的数据判断类型得到值
     *
     * @param
     */
    private static String getCellValue(Cell cell) {
        String cellValue = "";
        if (null != cell) {
            // 以下是判断数据的类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        Date theDate = cell.getDateCellValue();
                        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
                        cellValue = dff.format(theDate);
                    } else {
                        DecimalFormat df = new DecimalFormat("0");
                        cellValue = df.format(cell.getNumericCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = cell.getBooleanCellValue() + "";
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    cellValue = cell.getCellFormula() + "";
                    break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR:
                    cellValue = "非法字符";
                    break;
                default:
                    cellValue = "未知类型";
                    break;
            }
        }
        return cellValue;
    }

    /**
     * @Param:
     * @Author: ncjdjyh
     * @Description: 获取格式化后的属性值
     */
    private static String getPropertiesValue(String datePattern, Object o) {
        String propertiesValue;
        if (o == null) {
            propertiesValue = "";
        } else if (o instanceof Date) {
            propertiesValue = new SimpleDateFormat(datePattern).format(o);
        } else if (o instanceof Float || o instanceof Double) {
            //newScale 保留几位
            propertiesValue = new BigDecimal(o.toString()).setScale(9, BigDecimal.ROUND_HALF_UP).toString();
        } else {
            propertiesValue = o.toString();
        }
        return propertiesValue;
    }

    /**
     * @Param:
     * @Author: ncjdjyh
     * @Description: 检查表中的 indexMap 是否有实体相应的字段与之对应
     */
    public static <T> boolean validateEntityPerIndexMap(Map<Integer, String> indexMap, Class<T> clazz) {
//        Field[] thisFields = clazz.getDeclaredFields();
//        Field[] superFields = clazz.getSuperclass().getDeclaredFields();
//        List allFieldsList = Arrays.asList(thisFields, superFields);
//        ArrayList<Field> allFields =
//                (ArrayList) allFieldsList.stream()
//                        .flatMap(e -> Arrays.stream((Field[]) e))
//                        .collect(Collectors.toList());
//        HashSet<String> fieldSet = new HashSet<>();
//        for (Field field : allFields) {
//            fieldSet.add(field.getName());
//        }
        Set<String> fieldSet = new HashSet<>();
        getValidateFieldNames(clazz,fieldSet,new String(),new HashSet<>());
        for (String v : indexMap.values()) {
            if (!fieldSet.contains(v)) {
                return false;
            }
        }
        return true;
    }

    //支持使用【.】获取类成员属性
    private static void getValidateFieldNames(Class clazz,Set<String> validateValues,String prefix,Set<Class> removeClazz){
        if(removeClazz.contains(clazz)){
            return ;
        }
        //防止类相互调用为成员变量
        removeClazz.add(clazz);
        if(clazz.getSuperclass()!=null){
            getValidateFieldNames(clazz.getSuperclass(),validateValues,prefix,removeClazz);
        }
        for(Field field : clazz.getDeclaredFields()){
            if(field.getType().getName().contains(".")){
                if(field.getType().getName().substring(0,5).equals("java.")){
                    validateValues.add(prefix+field.getName());
                }else{
                    String prefixTemp = prefix+field.getName()+".";
                    getValidateFieldNames(field.getType(),validateValues,prefixTemp,removeClazz);
                }
            }else{
                validateValues.add(prefix+field.getName());
            }
        }
    }

}
