package com.dbc.framework.listener;

import com.dbc.framework.annotation.XxgScanner;
import com.dbc.framework.core.io.ClassPathResource;
import com.dbc.framework.core.manager.XxgAutoWiredManager;
import com.dbc.framework.core.manager.XxgControllerManager;
import com.dbc.framework.factory.XmlComponentReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @Auther dbc
 * @Date 2020/9/28 19:06
 */
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        XxgControllerManager xxgDefaultManager = new XxgControllerManager();

        String configClassName = servletContextEvent.getServletContext().getInitParameter("config");
        try {
            Class appListenerClass = Class.forName(configClassName);
            XxgScanner xxgScanner = (XxgScanner)appListenerClass.getAnnotation(XxgScanner.class);
            if (xxgScanner != null) {
                try {
                    // 扫描controller类,初始化List
                    xxgDefaultManager.scannerClass(xxgScanner.value());
                    // IOC 注册Component类
                    // 创建时，默认直接扫描配置文件
                    XxgAutoWiredManager xxgAutoWiredManager = new XxgAutoWiredManager(new ClassPathResource("ioc.xml"));
                    xxgAutoWiredManager.scannerClass(xxgScanner.value()); // 扫描@Component 以及 @XxgMapper
                    // 给Controller中的@AutoWired的属性赋值
                    xxgAutoWiredManager.registerControllerAutoWired();
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
