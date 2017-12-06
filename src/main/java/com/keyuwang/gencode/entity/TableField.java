package com.keyuwang.gencode.entity;

/**
 * Created by keyuwang on 2016/11/29.
 */
public class TableField {
    private String field;//字段名称
    private String type;//字段类型
    private String comment;//备注

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "TableField{" +
                "field='" + field + '\'' +
                ", type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
