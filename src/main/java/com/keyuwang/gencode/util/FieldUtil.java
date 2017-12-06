package com.keyuwang.gencode.util;

import com.keyuwang.gencode.entity.FieldInfo;
import com.keyuwang.gencode.entity.TableField;

import java.util.*;

/**
 * Created by wky77 on 2017/8/2.
 */
public class FieldUtil {
    /**
     * 获取类名称
     *
     * @param tableName
     * @return
     */
    public static String getClassname(String tableName) {
        String[] names = tableName.split("_");
        if (names.length == 1) {
            StringBuilder subword = new StringBuilder(names[0]);
            subword.setCharAt(0, Character.toUpperCase(names[0].charAt(0)));
            return subword.toString();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < names.length; i++) {
            StringBuilder subword = new StringBuilder(names[i]);
            subword.setCharAt(0, Character.toUpperCase(names[i].charAt(0)));
            sb.append(subword);
        }
        return sb.toString();
    }

    public static Map<String, Object> getFields(List<TableField> list) {
        Map<String, String> packageMap = new HashMap<String, String>();
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
        for (TableField TableField : list) {
            FieldInfo fieldInfo = getFieldInfo(TableField);
            fieldList.add(fieldInfo);
            if (fieldInfo.getDatatype().equals("Date")) {
                packageMap.put("java.util.Date", "1");
            }
        }

        List<String> packageList = new ArrayList<String>();
        Set<String> keySet = packageMap.keySet();
        for (String pk : keySet) {
            packageList.add(pk);
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("packageList", packageList);
        resultMap.put("fieldList", fieldList);
        return resultMap;
    }

    public static FieldInfo getFieldInfo(TableField TableField) {
        FieldInfo fieldInfo = new FieldInfo();
        String fieldName = TableField.getField().toLowerCase();
        String[] names = fieldName.split("_");
        StringBuilder sb = null;
        String fieldname;
        String getName;
        String setName;
        if (names.length > 1) {
            fieldname = names[0];
            for (int i = 1; i < names.length; i++) {
                sb = new StringBuilder(names[i]);
                sb.setCharAt(0, Character.toUpperCase(names[i].charAt(0)));
                fieldname = fieldname + sb;
            }
        } else {
            fieldname = names[0];
        }
        sb = new StringBuilder(fieldname);
        sb.setCharAt(0, Character.toUpperCase(fieldname.charAt(0)));
        getName = "get" + sb.toString();
        setName = "set" + sb.toString();
        fieldInfo.setFieldname(fieldname);
        fieldInfo.setGetName(getName);
        fieldInfo.setSetName(setName);
        fieldInfo.setDatatype(getDatetype(TableField.getType()));
        fieldInfo.setComment(TableField.getComment());
        return fieldInfo;
    }

    /**
     * 获取数据类型
     *
     * @param type
     * @return
     */
    public static String getDatetype(String type) {
        if (type.startsWith("int")) {
            return "Integer";
        } else if (type.startsWith("varchar")) {
            return "String";
        } else if (type.startsWith("datetime")) {
            return "Date";
        } else if (type.startsWith("double")) {
            return "Double";
        } else if (type.startsWith("float")) {
            return "Float";
        } else {
            return "String";
        }
    }
}
