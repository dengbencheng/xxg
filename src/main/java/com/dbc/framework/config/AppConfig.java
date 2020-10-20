package com.dbc.framework.config;

import com.dbc.framework.annotation.XxgScanner;

import java.io.IOException;
import java.util.Properties;

/**
 * @Auther dbc
 * @Date 2020/9/29 10:40
 */
@XxgScanner("com.dbc.meeting_system")
public class AppConfig {
    private static Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(AppConfig.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getControllerClassName() {
        return PROPERTIES.getProperty("dbc.controllerClassName");
    }
}
