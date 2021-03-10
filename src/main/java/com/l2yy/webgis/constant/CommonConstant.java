package com.l2yy.webgis.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/3 8:33 下午
 * @description：
 */
public class CommonConstant {

    public static final byte HOT = 1;

    public static final byte NOT_HOT = 0;

    public static final double PAGE_SIZE = 60;

    public static final double FTX_PAGE_SIZE = 20;

    public static final byte HAVE_DATA = 1;

    public static final byte HAVE_NO_DATA = 0;

    public static final int NO_CITY = 3;

    public static final int NO_DATA = 1;

    public static final int EXIST_DATA = 2;

    public static final byte NOT_DELETED = 0;

    public static final byte DELETED = 1;

    public static final byte AJK_DATA = 0;

    public static final byte FTX_DATA = 1;


    public static final int ADMIN = 1;
    public static final int EDITOR = 0;
    //    public static final byte METRIC_SRC_API = 3;
//    public static final byte METRIC_SRC_ES = 4;
    public static final Map<Integer, String> AUTH;

    static {
        Map<Integer, String> srcMap = new HashMap<>();
        srcMap.put(ADMIN, "admin");
        srcMap.put(EDITOR, "editor");
//        srcMap.put(METRIC_SRC_API, "API");
//        srcMap.put(METRIC_SRC_ES, "ES");
        AUTH = Collections.unmodifiableMap(srcMap);
    }


}
