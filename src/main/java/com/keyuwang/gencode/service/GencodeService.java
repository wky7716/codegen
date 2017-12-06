package com.keyuwang.gencode.service;

import com.keyuwang.gencode.dao.DBHelper;
import com.keyuwang.gencode.entity.TableField;
import com.keyuwang.gencode.util.FieldUtil;
import com.keyuwang.gencode.util.StringUtil;
import com.keyuwang.gencode.util.TemplateUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by keyuwang on 2016/11/29.
 */
public class GencodeService {

    private static String tableName = "";
    private static String packageName = "";
    private static String codeTargetPath = "";
    private static String filePath = "";
    private static String className = "";


    /**
     * 加载配置文件
     *
     * @date 2017/8/3 11:35
     * @author keyuwang
     */
    static {
        /*
            1、获取jar包的运行路径，在此路径的下获取数据配置文件gencode.properties 和 生成代码的存放路径
            2、加载gencode.properties属性配置文件，并将属性信息初始化到相关类静态变量
         */
        codeTargetPath = System.getProperty("user.dir");
        String configFilePath = System.getProperty("user.dir") + "/gencode.properties";
        Properties properties = new Properties();
        try {
            Reader reader = new FileReader(configFilePath);
            properties.load(reader);
            String dbUrl = (String) properties.get("dbUrl");
            String userName = (String) properties.get("userName");
            String userPwd = (String) properties.get("userPwd");
            String tableName = (String) properties.get("tableName");
            packageName = (String) properties.get("packageName");
            DBHelper.userName = userName.trim();
            DBHelper.userPwd = userPwd.trim();
            DBHelper.dbUrl = dbUrl.trim();
            GencodeService.tableName = tableName.trim();
//            GencodeService.packageName = packageName.trim() + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成代码主方法
     *
     * @date 2017/8/3 11:51
     * @author keyuwang
     */
    public static void gencode() {
        /*
            1、判断表信息与包名信息是否完整
            2、查询表中表中字段列表，包含字段名称、字段类型，字段备注
            3、依照约定规则生成类名称
            4、创建代码生成后的存储路径
            5、根据需求逐个生成类文件
         */
        if (StringUtil.isNotEmpty(tableName)) {
            if (StringUtil.isEmpty(packageName)) {
                packageName = FieldUtil.getClassname(tableName).toLowerCase();
            }
            DBHelper dbHelper = new DBHelper();
            List<TableField> fieldList = dbHelper.getColumns(tableName);
            className = FieldUtil.getClassname(tableName);
            filePath = codeTargetPath + File.separator + className + System.currentTimeMillis();
            File file = new File(filePath);
            file.mkdirs();
            // 1、生成entity
            genEntity(fieldList);//genEntity(filePath, tableName, list);
            // 2、生成auto dao
            genAutoMapper(fieldList);
//            // 3、生成manual dao
            genManualMapper(fieldList);
            // 4、生成service
            genService(fieldList);
            // 5、生成provider
            genProvider(fieldList);
//            // 6、生成controller
////            genController(filePath, tableName, list, packageName);
//            String targetPath = genPath + "/" + System.currentTimeMillis() + ".zip";
//            ZipUtil.compress(targetPath, filePath);
//            return targetPath;
        }
    }

    /**
     * 生成Java对应的实体信息
     *
     * @param list table对应的字段列表
     * @return
     * @date 2017/8/3 12:31
     * @author keyuwang
     */
    public static void genEntity(List<TableField> list) {
        /*
            1、获取生成表对应实体模板信息
            2、根据模板生成文件
         */

        try {
            Template template = TemplateUtil.getTemplateByName("entity.vm");
            VelocityContext ctx = new VelocityContext();
            String className = FieldUtil.getClassname(tableName);
            ctx.put("className", className);
            Map<String, Object> resultMap = FieldUtil.getFields(list);
            ctx.put("packageList", resultMap.get("packageList"));
            ctx.put("fieldList", resultMap.get("fieldList"));
            StringWriter writer = new StringWriter();
            template.merge(ctx, writer);
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filePath + "/" + className + ".java", true), "UTF-8");
            osw.write(writer.toString());
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成固定mapper对应的 xml和java文件
     *
     * @param list 字段列表
     * @return
     * @date 2017/8/3 12:38
     * @author keyuwang
     */
    private static void genAutoMapper(List<TableField> list) {
        /*
            1、声明velocity context 并设置代码生成时需要用到的参数
            2、获取固定mapper对应的xml模板，并根据模板生成xml文件
            3、获取固定mapper对应的java模板，并根据模板生成java文件
         */

        try {
            //封装参宿
            VelocityContext ctx = new VelocityContext();
            //TODO 包名可否传参数解决
            ctx.put("namespace", "com.kingstardi.dao." + packageName + "." + FieldUtil.getClassname(tableName) + "Mapper");// 设置路径
            ctx.put("resultClass", "com.kingstardi.entity.core." + className);
            ctx.put("tableName", tableName);
            ctx.put("path", packageName);// 设置路径
            ctx.put("className", className);// 类名称
            Map<String, Object> resultMap = FieldUtil.getFields(list);
            ctx.put("packageList", resultMap.get("packageList"));
            ctx.put("fieldList", resultMap.get("fieldList"));

            //生成固定xml文件
            Template template = TemplateUtil.getTemplateByName("automapper.vm");
            StringWriter writer = new StringWriter();
            template.merge(ctx, writer);
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filePath + "/" + className + "Mapper.xml", true), "UTF-8");
            osw.write(writer.toString());
            osw.flush();
            osw.close();

            //生成固定java文件
            template = TemplateUtil.getTemplateByName("autojava.vm");
            writer = new StringWriter();
            template.merge(ctx, writer);
            osw = new OutputStreamWriter(new FileOutputStream(filePath + "/" + className + "Mapper.java", true), "UTF-8");
            osw.write(writer.toString());
            osw.flush();
            osw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成可手动修改的mapper对应的 xml和java文件
     *
     * @param list 字段列表
     * @date 2017/8/3 15:50
     * @author keyuwang
     */
    private static void genManualMapper(List<TableField> list) {

        try {
            Template template = TemplateUtil.getTemplateByName("manualmapper.vm");
            VelocityContext ctx = new VelocityContext();
            ctx.put("namespace", "com.kingstardi.dao." + packageName + "." + className + "ManualMapper");// 设置路径
            ctx.put("resultClass", "com.kingstardi.entity.core." + className);
            ctx.put("tableName", tableName);
            ctx.put("path", packageName);// 设置路径
            ctx.put("className", className);// 类名称
            Map<String, Object> resultMap = FieldUtil.getFields(list);
            ctx.put("packageList", resultMap.get("packageList"));
            ctx.put("fieldList", resultMap.get("fieldList"));
            StringWriter writer = new StringWriter();
            template.merge(ctx, writer);
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filePath + "/" + className + "ManualMapper.xml", true), "UTF-8");
            osw.write(writer.toString());
            osw.flush();
            osw.close();
            template = TemplateUtil.getTemplateByName("manualjava.vm");
            writer = new StringWriter();
            template.merge(ctx, writer);
            osw = new OutputStreamWriter(new FileOutputStream(filePath + "/" + className + "ManualMapper.java", true), "UTF-8");
            osw.write(writer.toString());
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成service
     *
     * @param list 字段列表
     * @date 2017/8/3 15:53
     * @author keyuwang
     */
    private static void genService(List<TableField> list) {
        try {
            Template template = TemplateUtil.getTemplateByName("service.vm");
            VelocityContext ctx = new VelocityContext();
            ctx.put("className", className);// 类名称
            StringWriter writer = new StringWriter();
            template.merge(ctx, writer);
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filePath + "/" + className + "Service.java", true), "UTF-8");
            osw.write(writer.toString());
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void genProvider(List<TableField> list) {
        try {
            Template template = TemplateUtil.getTemplateByName("provider.vm");
            VelocityContext ctx = new VelocityContext();
            ctx.put("path", packageName);// 设置路径
            ctx.put("className", className);// 类名称
            StringWriter writer = new StringWriter();
            template.merge(ctx, writer);
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filePath + "/" + className + "Provider.java", true), "UTF-8");
            osw.write(writer.toString());
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
//
//    private void genController(String targetPath, String tableName, List<TableField> list, String packageName) throws IOException {
//        Template template = getTemplateByName("controller.vm");
//        VelocityContext ctx = new VelocityContext();
//        ctx.put("path", packageName);// 设置路径
//        String className = getClassname(tableName);
//        ctx.put("className", className);// 类名称
//        StringBuilder sb = new StringBuilder(className);
//        sb.setCharAt(0, Character.toLowerCase(className.charAt(0)));
//        ctx.put("classNameLowerCase", sb.toString());// 类名称
//        StringWriter writer = getWriter(template, ctx);
//        targetPath = targetPath + "/web";
//        File file = new File(targetPath);
//        if (file.exists()) {
//            deleteDirectory(targetPath);
//        }
//        file.mkdir();
//
//        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(targetPath + "/" + className + "Controller.java", true), "UTF-8");
//        osw.write(writer.toString());
//        osw.flush();
//        osw.close();
//    }

//    private static Map<String, String> loadProperties() {
//
//    }

    public static void main(String[] args) {
        gencode();

//        GencodeService gencodeService = new GencodeService();
//        gencodeService.gencode("t_userinfo", "com.test1.test2.test3");
    }

}

