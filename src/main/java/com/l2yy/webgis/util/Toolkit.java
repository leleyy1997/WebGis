package com.l2yy.webgis.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Objects;

public class Toolkit {

    public static boolean isValid(String s) {
        if (s != null && !s.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInvalid(String s) {
        if (s == null || s.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumber(String s) {
        if (!isValid(s)) {
            return false;
        } else {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
    }

    public static boolean isDoubleNumber(String s) {
        if (!isValid(s)) {
            return false;
        } else {
            try {
                Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
    }

    public static void sleep(int seconds) {
        for (int i=0; i < seconds; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }

    public static void threadSleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (Exception e) {
        }
    }

    public static boolean isJson(String s) {
        try {
            Object json = JSON.parse(s);
            if(json instanceof JSONObject){
                return true;
            }else if (json instanceof JSONArray){
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static String[] stringToArr(String s) {
        return Toolkit.isValid(s) ? s.replaceAll("\"", "").replace("[", "")
                .replace("]", "").split(",") : new String[]{};
    }

    public static int[] changeIntArr(String[] arr) {
        try {
            int[] result = new int[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = Integer.parseInt(arr[i]);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isNull(Object... objects) {
        if (objects == null) {
            return false;
        }
        for (Object o1 : objects) {
            if (Objects.isNull(o1)) {
                return true;
            }
            if (o1 instanceof String) {
                return isInvalid((String) o1);
            }
        }
        return false;
    }

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    public static boolean noNull(Object... objects) {
        return !isNull(objects);
    }
}
