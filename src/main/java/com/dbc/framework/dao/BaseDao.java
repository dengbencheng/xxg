package com.dbc.framework.dao;

import com.dbc.framework.utils.DaoUtils;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Auther dbc
 * @Date 2020/9/23 10:13
 */
public interface BaseDao<T> {
    QueryRunner runner = DaoUtils.getRunner();

    /**
     * 获取数据总数
     * @return
     */
    int getCount() throws SQLException;

    /**
     * 根据id获取实体信息
     * @param id
     */
    T selectById(String id) throws SQLException;

    /**
     * 根据id删除数据
     * @param id
     * @return
     */
    void removeById(String id) throws SQLException;

    /**
     * 根据id删除数据
     * @param id
     * @return
     */
    void removeById(Connection coon, String id) throws SQLException;
    /**
     * 根据传入的实体id修改其数据
     * @param t
     * @return
     */
    void updateById(T t) throws SQLException, IllegalAccessException;

    /**
     * 根据传入的实体id修改其数据
     * @param t
     * @return
     */
    void updateById(Connection conn, T t) throws SQLException, IllegalAccessException;

    /**
     * 插入信息
     * @param t
     * @return
     */
    void insert(T t) throws SQLException, IllegalAccessException;

    /**
     * 插入信息
     * @param t
     * @return
     */
    void insert(Connection conn, T t) throws IllegalAccessException, SQLException;
    /**
     * 插入信息
     * @param t
     * @return
     */
    void insert(List<T> t) throws IllegalAccessException, SQLException;
    /**
     * 插入信息
     * @param t
     * @return
     */
    void insert(Connection conn, List<T> t) throws IllegalAccessException, SQLException;
}
