package com.dilusense.custom_camera;

public class StringUtils {
    public static boolean isEmpty(Object obj) {
        return (obj == null) || (clean(obj.toString()).length() == 0);
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static String clean(String str) {
        return str == null ? "" : str.trim();
    }

    public static boolean isNullOrEmpty(String s) {
        if (s == null || "".equals(s)) {
            return true;
        } else {
            return false;
        }
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String UIDGenerator() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    public static WH getWHRatio(String ratioStr, String divideStr) {
        String[] whs = ratioStr.split(divideStr);

        int w =(int)Integer.parseInt(whs[0]);
        int h =(int)Integer.parseInt(whs[1]);
        return new WH(w,h);
    }

}
