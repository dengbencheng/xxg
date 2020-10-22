package com.dbc.framework.contanier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther dbc
 * @Date 2020/10/22 9:37
 * @Description
 */
public class SingletonContainer {
    private Map<Class, Object> singletonMap = new ConcurrentHashMap<>();
    private static SingletonContainer instance = null;

    private SingletonContainer() {

    }

    public static SingletonContainer getSingletonContainer() {
        if (instance == null) {
            synchronized (SingletonContainer.class) {
                instance = new SingletonContainer();
            }
        }
        return instance;
    }

    public void addInstance(Class clazz, Object instance) {
        singletonMap.put(clazz, instance);
    }

    public Object getInstance(Class clazz) {
        return singletonMap.get(clazz);
    }
}
