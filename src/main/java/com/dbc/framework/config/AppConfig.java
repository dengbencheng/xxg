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
    private static final String CONTROLLER_PACKET = "dbc.controllerPacket";
    private static final String MAPPER_PACKET = "dbc.mapperPacket";

    static {
        try {
            PROPERTIES.load(AppConfig.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getControllerPacket() {
        return PROPERTIES.getProperty(CONTROLLER_PACKET);
    }
    public static String getMapperPacket() {
        return PROPERTIES.getProperty(MAPPER_PACKET);
    }
}
