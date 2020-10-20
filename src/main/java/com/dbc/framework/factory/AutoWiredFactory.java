package com.dbc.framework.factory;

import com.dbc.framework.annotation.XxgAutoWired;
import com.dbc.framework.annotation.XxgComponent;
import com.dbc.framework.contanier.ComponentContainer;
import com.dbc.framework.contanier.MapperContainer;
import com.dbc.framework.exception.AutoWiredException;
import com.dbc.framework.pojo.BeanDefinition;
import com.dbc.framework.utils.MapperProxyHandler;

import java.lang.reflect.Field;

/**
 * @Auther dbc
 * @Date 2020/10/20 11:47
 * @Description 处理AutoWired注解
 */
public class AutoWiredFactory {
    private static ComponentContainer componentContainer = ComponentContainer.getInstance(); // component类容器
    private static MapperContainer mapperContainer = MapperContainer.getInstance();


    /**
     * 处理controller的属性，自动注入值, 返回该类的一个实例
     */
    public static Object getDiInstance(Class clazz, Object instance) {
        Field[] fields = clazz.getDeclaredFields();
        if (instance == null) {
            try {
                instance = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for(Field field : fields) {
            if (field.isAnnotationPresent(XxgAutoWired.class)) {
                // 给有XxgAutoWired注解的属性,自动注入值
                field.setAccessible(true);
                try {
                    BeanDefinition beanDefinition = componentContainer.getComponentDefinition(field.getType());
                    if (beanDefinition != null) {
                        // 处理@XxgComponent 的自动注入
                        field.set(instance, getDiInstance(beanDefinition.getTypeClazz(), null));
                    } else {
                        // @Mapper注解
                        beanDefinition = mapperContainer.getMapperDefinition(field.getType());
                        if (beanDefinition == null) {
                            throw new AutoWiredException("未找到相应注解的处理方法");
                        }
                        // 注册dao接口参数的动态代理对象
                        MapperProxyHandler mapperProxyHandler = new MapperProxyHandler<>(beanDefinition.getTypeClazz());
                        field.set(instance, mapperProxyHandler.getProxy());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new AutoWiredException("DI自动注入时异常", e);
                }
            }
        }
        return instance;
    }
}
