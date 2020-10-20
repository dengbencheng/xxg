package com.dbc.framework.dao.impl;

import com.dbc.framework.dao.BaseDao;
import com.dbc.framework.utils.DaoUtils;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Auther dbc
 * @Date 2020/9/23 10:16
 */
public class BaseDaoImpl<T> implements BaseDao<T> {
    private Class<T> clazz;


    public BaseDaoImpl(Class<T> clazz) {
        this.clazz = clazz;
    }


    @Override
    public int getCount() throws SQLException {
        String sql = DaoUtils.getCountSql(clazz);
        Number count = 0;
        // 使用ScalarHandler接收单行数据。
        // 返回数据可能是int,可能是long,故用他们两个的父类Number来接收
        count = runner.query(sql, new ScalarHandler<>());
        return count.intValue();
    }
    @Override
    public T selectById(String id) throws SQLException {
        T t = null;
        String selectSql = DaoUtils.getRemoveOrSelectSql(clazz, "select");
        Object[] params = {id};
        t = runner.query(DaoUtils.getConnection(), selectSql, new BeanHandler<>(clazz), params);
        return t;
    }

    @Override
    public void removeById(String id) throws SQLException {
        this.removeById(DaoUtils.getConnection(), id);
    }

    @Override
    public void removeById(Connection conn, String id) throws SQLException {
        String removeSql = DaoUtils.getRemoveOrSelectSql(clazz, "remove");
        Object[] params = {id};
        runner.update(conn, removeSql, params);
    }

    @Override
    public void updateById(T t) throws SQLException, IllegalAccessException {
        this.updateById(DaoUtils.getConnection(), t);
    }

    @Override
    public void updateById(Connection conn,T t) throws SQLException, IllegalAccessException {
        Map<String, Object> updateSql = DaoUtils.getUpdateByIdSql(t);
        String sql = (String) updateSql.get("sql");
        Object[] params = (Object[]) updateSql.get("params");
        runner.update(conn, sql, params);
    }

    @Override
    public void insert(T t) throws SQLException, IllegalAccessException {
        this.insert(DaoUtils.getConnection(), t);
    }

    @Override
    public void insert(Connection conn, T t) throws IllegalAccessException, SQLException {
        Map<String, Object> insertSql = DaoUtils.getInsertSql(t);
        String sql = (String) insertSql.get("sql");
        Object[] params = (Object[]) insertSql.get("params");
        runner.update(conn, sql, params);
    }

    @Override
    public void insert(List<T> t) throws IllegalAccessException, SQLException {
        this.insert(DaoUtils.getConnection(), t);
    }

    @Override
    public void insert(Connection conn, List<T> t) throws IllegalAccessException, SQLException {
        for(T item : t) {
            if (conn != null) {
                this.insert(conn, t);
            } else {
                this.insert(DaoUtils.getConnection(), t);
            }
        }
    }
}
