package com.dbc.framework.contanier;

import com.dbc.framework.annotation.*;
import com.dbc.framework.factory.AutoWiredFactory;
import com.dbc.framework.pojo.*;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于存放请求路径 与 controller对应关系的类
 * 设计成单例模型
 */
public class RequestPathContainer {
    private static List<BeanDefinition> requestList = new ArrayList<>();

    private static volatile RequestPathContainer instance = null;

    public static RequestPathContainer getInstance() {
        if (instance == null) {
            synchronized(RequestPathContainer.class){
                if (instance == null) {
                    instance = new RequestPathContainer();
                }
            }
        }
        return instance;
    }

    private RequestPathContainer() {

    }

    /**
     * 获取所有请求的类
     */
    public List<BeanDefinition> getRequestList() {
        return requestList;
    }


    /**
     *  根据uri 和 请求方法 获取执行方法
     */
    public MethodDefinition getMethodDefinition(String uri, String method) {
        for (BeanDefinition beanDefinition: requestList) {
            if (!uri.contains(beanDefinition.getControllerUrlPath())) {
                continue;
            }
            List<MethodDefinition> methodDefinitions = beanDefinition.getMethodDefinitions();
            for (MethodDefinition methodDefinition: methodDefinitions) {
                StringBuilder sb = new StringBuilder().append(beanDefinition.getControllerUrlPath());
                sb.append(methodDefinition.getRequestMappingUrlPath());
                if (!sb.toString().equals(uri)) {
                    continue;
                }
                String[] allowedRequestMethods = methodDefinition.getAllowedRequestMethods();
                for (String str : allowedRequestMethods) {
                    if (str.toUpperCase().equals(method.toUpperCase())) {
                        // 请求路径 与 请求方法 均满足,返回该方法描述类
                        return methodDefinition;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 添加
     */
    public void addBeanDefinition(BeanDefinition beanDefinition) {
        requestList.add(beanDefinition);
    }







    /**
     * 获取Mapper接口的代理对象
     */
//    private Object registerProxyInstance(MapperDefinition mapperDefinition) {
//        Class clazz = mapperDefinition.getTypeClazz();
//        return Proxy.newProxyInstance(
//                clazz.getClassLoader(),
//                new Class[]{clazz},
//                new InvocationHandler() {
//                    @Override
//                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                        System.out.println("11111");
//                        return null;
//                    }
//                });
//    }
}
