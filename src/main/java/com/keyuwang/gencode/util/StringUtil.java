package com.keyuwang.gencode.util;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wky77 on 2017/8/2.
 */
public class StringUtil {

    /**
     * 判断字符串是否为空：
     *
     * @param str
     * @return true:不为空 ;false:为空
     * @deprecated by ewfu@2012-12-24 请使用isNotEmpty代替
     */
    public static boolean stringEmptyCheck(String str) {
        if (str != null && str.length() > 0)
            return true;
        else
            return false;
    }

    /**
     * 判断字符串不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        if (str != null && str.trim().length() > 0)
            return true;
        else
            return false;
    }

    /**
     * 判断字符串为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.trim().length() < 1)
            return true;
        else
            return false;
    }

    /**
     * @param sourceStr
     * @return
     */
    public static String convertForView(String sourceStr) {
        if (!isNotEmpty(sourceStr)) {
            return "";
        }
        // String destStr = sourceStr.replaceAll(/\t/g, "&nbsp;&nbsp;");
        String destStr = "<pre>" + sourceStr + "</pre>";
        return destStr;
    }

    /**
     * @param objInst
     * @return
     */
    public static String fromObject(Object objInst) {
        if (null != objInst) {
            if (objInst instanceof String) {
                return (String) objInst;
            } else if (objInst instanceof Timestamp) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.format(new Date(((Timestamp) objInst).getTime()));
            } else {
                return objInst + "";
            }
        }
        return null;
    }

    /**
     * @param str
     * @return
     */
    public static String trimNull(String str) {
        if (StringUtil.isEmpty(str)) {
            return "";
        }
        return str;
    }

    /**
     * 字符串转utf8编码
     *
     * @param srcStr
     * @return
     */
    public static String encode2Utf8(String srcStr) {
        if (isNotEmpty(srcStr)) {
            StringBuffer output = new StringBuffer();
            for (int i = 0; i < srcStr.length(); i++) {
                output.append("\\u" + Integer.toString(srcStr.charAt(i), 16));
            }
            return output.toString();
        }
        return "";
    }

    /**
     * 解码：对于前台encodeURIComponent后的数据
     *
     * @param str
     * @return
     */
    public static String decode(String str) {
        try {
            return java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 根据某个特殊字符charStr分割的字符串str， 拼接Ibatis字符串类型参数
     *
     * @param chatStr str
     */
    public static String getParamStr(String str, String segStr) {
        if (isNotEmpty(str)) {
            String resStr = "";
            String[] arrStr = str.split(segStr);
            for (int i = 0; i < arrStr.length; i++) {
                if (i != arrStr.length - 1) {
                    resStr += "'" + arrStr[i] + "'" + segStr;
                    continue;
                }
                resStr += "'" + arrStr[i] + "'";
            }
            return resStr;
        }
        return null;
    }
}
