package com.dbc.framework.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @Auther dbc
 * @Date 2020/10/19 20:35
 * @Description mapper接口代理类
 */
public class MapperProxyHandler<T> implements InvocationHandler {
    private Class<T> clazz;

    public MapperProxyHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        System.out.println(Arrays.toString(method.getParameters()));
        System.out.println(method.getName());
        System.out.println(Arrays.toString(args));
        for(Class clazz : parameterTypes) {
            System.out.println(clazz.getTypeName());
        }
        System.out.println("!11");
        return null;
    }

    public T getProxy(){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
    }
}
