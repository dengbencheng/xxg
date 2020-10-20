package com.dbc.framework.utils;

import com.dbc.framework.annotation.XxgTable;
import com.dbc.framework.annotation.XxgTableId;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther dbc
 * @Date 2020/9/23 10:19
 */
public class DaoUtils {
    private static QueryRunner runner = null;
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    static {
        runner = new QueryRunner(dataSource);
    }


    /**
     * 回滚事务
     */
    public static void rollbackTransaction() throws SQLException {
        Connection conn = getConnection();
        if (!conn.getAutoCommit()) {
            conn.rollback();
            conn.close();
            threadLocal.remove();
        }
    }

    public static void startTransaction() throws SQLException {
        Connection conn = getConnection();
        if (conn.getAutoCommit()) {
            conn.setAutoCommit(false);
        }
    }

    public static void commitTransaction() throws SQLException {
        Connection conn = getConnection();
        if (!conn.getAutoCommit()) {
            conn.commit();
            conn.close();
            threadLocal.remove();
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 获取QueryRunner
     */
    public static QueryRunner getRunner() {
        return runner;
    }

    /**
     * 获取连接
     */
    public static Connection getConnection() {
        Connection conn = threadLocal.get();
        if (conn == null) {
            try {
                conn = dataSource.getConnection();
                threadLocal.set(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static <T> String getCountSql(Class<T> clazz) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ");
        if (!clazz.isAnnotationPresent(XxgTable.class)) {
            throw new RuntimeException("没有Table注解");
        }
        XxgTable table = (XxgTable) clazz.getDeclaredAnnotation(XxgTable.class);
        sql.append(table.tableName());
        return sql.toString();
    }

    /**
     * 获取插入的sql语句 即参数列表params
     * @param t
     * @param <T>
     * @return String sql, Object[] params
     * @throws IllegalAccessException
     */
    public static <T> Map<String, Object> getInsertSql(T t) throws IllegalAccessException {
        Class clazz = t.getClass();
        Map<String, Object> result = new HashMap<>();
        StringBuilder sql = new StringBuilder().append("INSERT INTO ");
        if (!clazz.isAnnotationPresent(XxgTable.class)) {
            throw new RuntimeException("没有Table注解");
        }
        XxgTable table = (XxgTable)clazz.getDeclaredAnnotation(XxgTable.class);
        sql.append(table.tableName()).append("(");
        Field[] fields = clazz.getDeclaredFields();
        Object[] params = new Object[fields.length];
        int i = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            sql.append(field.getName()).append(",");
            params[i++] = field.get(t);
        }
        String substring = sql.substring(0, sql.length() - 1);
        sql = new StringBuilder();
        sql.append(substring).append(") VALUES(");
        for (int j = 0; j < fields.length; j++) {
            sql.append("?,");
        }
        substring = sql.substring(0, sql.length() - 1);
        result.put("sql", substring + ")");
        result.put("params", params);
        return result;
    }

    /**
     * 根据type 获取根据id查询或删除的sql语句
     * @param clazz
     * @param type
     * @param <T>
     * @return
     */
    public static <T> String getRemoveOrSelectSql(Class<T> clazz, String type) {
        StringBuilder sb = new StringBuilder();
        if (type.equals("remove")) {
            sb.append("DELETE FROM ");
        } else if (type.equals("select")) {
            sb.append("SELECT * FROM ");
        } else {
            throw new RuntimeException("没有此方法");
        }
        XxgTable table = clazz.getDeclaredAnnotation(XxgTable.class);
        if (table == null) {
            throw new RuntimeException("没有Table注解");
        }
        sb.append(table.tableName()).append(" WHERE ");
        Field[] fields = clazz.getDeclaredFields();
        String id = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(XxgTableId.class)) {
                id = field.getName();
                break;
            }
        }
        if (id == null) {
            throw new RuntimeException("没有TableId注解");
        }
        sb.append(id).append("=?");
        return sb.toString();
    }


    /**
     * 获取update的sql语句即参数
     * @param t 修改的实例对象
     * @return Map<String, Object>  String sql, Object[] params
     */
    public static <T> Map<String, Object> getUpdateByIdSql(T t) throws IllegalAccessException {
        Map<String, Object> result = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        Class clazz = t.getClass();
        if (!clazz.isAnnotationPresent(XxgTable.class)) {
            throw new RuntimeException("没有指定数据库表名,无法自动生成update的sql语句");
        }
        XxgTable table = (XxgTable) clazz.getAnnotation(XxgTable.class);
        sql.append("UPDATE ").append(table.tableName()).append(" SET ");
        StringBuilder whereSql = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        Object[] params = new Object[fields.length];
        int i = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(XxgTableId.class)) {
                // id
                whereSql.append(" WHERE ").append(field.getName()).append("=").append("?");
                params[fields.length - 1] = field.get(t);
            } else {
                sql.append(field.getName()).append("=").append("?").append(",");
                params[i++] = field.get(t);
            }
        }
        if (whereSql.length() == 0) {
            throw new RuntimeException("没有TableId注解");
        }
        result.put("sql", sql.substring(0, sql.length()-1) + whereSql.toString());
        result.put("params", params);
        return result;
    }
}
