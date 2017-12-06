package com.keyuwang.gencode.dao;

import com.keyuwang.gencode.entity.TableField;
import com.keyuwang.gencode.util.StringUtil;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wky77 on 2017/7/30.
 */
public class DBHelper {
    public static final Logger logger = Logger.getLogger(DBHelper.class);

    public static String dbUrl = "";
    public static String userName = "";
    public static String userPwd = "";
    private Connection conn;

    private Connection getConn() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, userName, userPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public List<TableField> getColumns(String tableName) {
        conn = getConn();
        StringBuilder data = new StringBuilder();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TableField> tableFieldList = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder("show full columns from " + tableName + " ;");
            System.out.println(sql.toString());
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                TableField tableField = new TableField();
                String field = rs.getString("Field");
                tableField.setField(field);
                String type = rs.getString("Type");
                tableField.setType(type);
                String comment = rs.getString("Comment");
                if (StringUtil.isNotEmpty(comment)) {
                    tableField.setComment(comment);
                    tableFieldList.add(tableField);
                }
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return tableFieldList;
    }

    public static void main(String[] args) {
        DBHelper.userName = "admin";
        DBHelper.userPwd = "123456";
        DBHelper.dbUrl = "jdbc:mysql://192.168.3.250:3306/di_v5";
        String tableName = "t_userinfo";
        DBHelper dbHelper = new DBHelper();
        List<TableField> fieldList = dbHelper.getColumns(tableName);
        for (TableField tableField : fieldList) {
            System.out.println(tableField.toString());
        }
    }


}
