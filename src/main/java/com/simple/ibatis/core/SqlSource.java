package com.simple.ibatis.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author xiabing
 * @Desc 将sql语句拆分为sql部分和参数部分
 * @example select * from users where id = {user.id} and name = {user.name}
 * -> sql = select * from users where id = ? and name = ?
 * -> param = {user.id,user.name}
 **/
public class SqlSource {
    /**sql语句，待输入字段替换成?*/
    private String sql;
    /**待输入字段*/
    private List<String> param;
    /**select update insert delete*/
    private Integer executeType;

    public SqlSource(String sql){
        this.param = new ArrayList<>();
        this.sql = sqlInject(this.param,sql);
    }

    private String sqlInject(List<String> paramResult, String sql){

        String labelPrefix = "{";

        String labelSuffix = "}";

        while (sql.indexOf(labelPrefix) > 0 && sql.indexOf(labelSuffix) > 0){
            String sqlParamName = sql.substring(sql.indexOf(labelPrefix),sql.indexOf(labelSuffix)+1);
            sql = sql.replace(sqlParamName,"?");
            paramResult.add(sqlParamName.replace("{","").replace("}",""));
        }
        return sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getParam() {
        return param;
    }

    public void setParam(List<String> param) {
        this.param = param;
    }

    public Integer getExecuteType() {
        return executeType;
    }

    public void setExecuteType(Integer executeType) {
        this.executeType = executeType;
    }
}
