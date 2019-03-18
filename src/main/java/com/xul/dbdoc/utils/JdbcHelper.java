package com.xul.dbdoc.utils;

import com.xul.dbdoc.domain.Field;
import com.xul.dbdoc.domain.Table;
import com.xul.dbdoc.enums.QuerySQL;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxu on 2018/12/14.
 */
@Slf4j
public class JdbcHelper {

    private QuerySQL querySQL;
    private Connection connection;

    public static JdbcHelper connect(String db, String username, String password) {
        return new JdbcHelper(db, username, password, QuerySQL.MYSQL.getDbType());
    }

    public static JdbcHelper connect(String db, String username, String password, String dbType) {
        return new JdbcHelper(db, username, password, dbType);
    }

    private JdbcHelper(String db, String username, String password, String dbType) {
        querySQL = QuerySQL.getQuerySQL(dbType);
        String url = querySQL.getUrlTemplate();
        url = url.replace("${db}", db);
        connection = getConn(url, username, password);
    }

    private Connection getConn(String url, String username, String password) {
        Connection conn = null;
        try {
            Class.forName(querySQL.getDriverClassName());
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return conn;
    }


    public List<Table> getTableList() {
        PreparedStatement preparedStatement = null;
        List<Table> tableList = new ArrayList<>();
        try {
            //所有的表信息
            preparedStatement = connection.prepareStatement(querySQL.getTableCommentsSql());
            ResultSet results = preparedStatement.executeQuery();
            while (results.next()) {
                String tableName = results.getString(querySQL.getTableName());
                if (tableName.contains("20")) {
                    continue;
                }
                if (StringUtils.isNotEmpty(tableName)) {
                    String tableComment = results.getString(querySQL.getTableComment());
                    Table table = new Table();
                    table.setName(tableName);
                    table.setComment(tableComment == null ? "" : tableComment);
                    tableList.add(this.convertTableFields(table));
                } else {
                    System.err.println("当前数据库为空！！！");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return tableList;
    }

    private Table convertTableFields(Table table) {
        List<Field> fieldList = new ArrayList<>();
        try {
            String tableFieldsSql = querySQL.getTableFieldsSql();
            tableFieldsSql = String.format(tableFieldsSql, table.getName());
            PreparedStatement preparedStatement = connection.prepareStatement(tableFieldsSql);
            ResultSet results = preparedStatement.executeQuery();
            while (results.next()) {
                Field field = new Field();
                String key = results.getString(querySQL.getFieldKey());
                if (StringUtils.isNotEmpty(key) && key.toUpperCase().equals("PRI")) {
                    field.setKey("PK");
                }
                field.setName(results.getString(querySQL.getFieldName()));
                field.setType(results.getString(querySQL.getFieldType()));
                String fieldComment = results.getString(querySQL.getFieldComment());
                field.setComment(fieldComment == null ? "" : fieldComment);
                fieldList.add(field);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        table.setFieldList(fieldList);
        return table;
    }

}
