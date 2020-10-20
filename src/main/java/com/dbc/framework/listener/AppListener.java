package com.dbc.framework.listener;

import com.dbc.framework.annotation.XxgScanner;
import com.dbc.framework.XxgDefaultManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @Auther dbc
 * @Date 2020/9/28 19:06
 */
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String configClassName = servletContextEvent.getServletContext().getInitParameter("config");
        try {
            Class appListenerClass = Class.forName(configClassName);
            XxgScanner xxgScanner = (XxgScanner)appListenerClass.getAnnotation(XxgScanner.class);
            if (xxgScanner != null) {
                try {
                    XxgDefaultManager.scanner(xxgScanner.value()); // 扫描controller类,初始化List
                    XxgDefaultManager.registerAutoWired(); // IOC 注册Component类,给@AutoWired的属性赋值
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
