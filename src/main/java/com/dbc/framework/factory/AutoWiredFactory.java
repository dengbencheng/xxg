package com.dbc.framework.factory;

import com.dbc.framework.annotation.XxgAutoWired;
import com.dbc.framework.contanier.ComponentContainer;
import com.dbc.framework.contanier.MapperContainer;
import com.dbc.framework.contanier.SingletonContainer;
import com.dbc.framework.exception.AutoWiredException;
import com.dbc.framework.pojo.BeanDefinition;
import com.dbc.framework.pojo.PropertyValue;
import com.dbc.framework.pojo.RuntimeBeanReference;
import com.dbc.framework.pojo.TypeStringValue;
import com.dbc.framework.utils.MapperProxyHandler;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Auther dbc
 * @Date 2020/10/20 11:47
 * @Description 处理AutoWired注解
 */
public class AutoWiredFactory {
    private static ComponentContainer componentContainer = ComponentContainer.getInstance(); // component类容器
    private static MapperContainer mapperContainer = MapperContainer.getInstance();
    private static SingletonContainer singletonContainer = SingletonContainer.getSingletonContainer(); // 单例的实例容器


    /**
     * 处理controller的属性，自动注入值, 返回该类的一个实例
     * @param beanDefinition 要获取实例的 类的包装类
     */
    public static Object getDiInstance(BeanDefinition beanDefinition) {
        if (beanDefinition == null) {
            throw new AutoWiredException("传入的类为空");
        }
        Class clazz = beanDefinition.getTypeClazz();
        Field[] fields = clazz.getDeclaredFields();
        Object instance = createInstance(beanDefinition);
        for(Field field : fields) {
            if (field.isAnnotationPresent(XxgAutoWired.class)) {
                // 给有XxgAutoWired 注解的属性,自动注入值
                field.setAccessible(true);
                try {
                    BeanDefinition componentBeanDefinition = componentContainer.getBeanDefinition(field.getType());
                    if (componentBeanDefinition != null) {
                        if (componentBeanDefinition.isXmlDependency()) {
                            // 处理 xml文件中的注入方式
                            field.set(instance, getXmlDiInstance(componentBeanDefinition));
                        }else if (componentBeanDefinition.getAnnotation() != null) {
                            // 处理@XxgComponent 的自动注入
                            field.set(instance, getDiInstance(componentBeanDefinition));
                        }
                    } else {
                        // @Mapper注解
                        BeanDefinition mapperBeanDefinition = mapperContainer.getMapperDefinition(field.getType());
                        if (mapperBeanDefinition == null) {
                            throw new AutoWiredException(field.getType() + "未找到相应注解的处理方法");
                        }
                        // 注册dao接口参数的动态代理对象
                        MapperProxyHandler mapperProxyHandler = new MapperProxyHandler<>(mapperBeanDefinition.getTypeClazz());
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

    /**
     * 处理 xml文件中的注入方式 返回实例
     */
    private static Object getXmlDiInstance(BeanDefinition componentBeanDefinition) throws ClassNotFoundException, IntrospectionException, InvocationTargetException, IllegalAccessException {
        Object instance = createInstance(componentBeanDefinition);
        registerProperty(instance, componentBeanDefinition); // 给property赋值
        return instance;
    }

    /**
     * 给property赋值
     */
    private static void registerProperty(Object instance, BeanDefinition componentBeanDefinition) throws IntrospectionException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        List<PropertyValue> propertyValueList = componentBeanDefinition.getPropertyValueList();
        BeanInfo beanInfo = Introspector.getBeanInfo(componentBeanDefinition.getTypeClazz());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyValue pv : propertyValueList) {
            Object value = pv.getValue();
            String pName = pv.getPName();
            String javaType = pv.getJavaType();
            for (PropertyDescriptor pd : propertyDescriptors) {
                // 参数名称和类型一致时
                if (pd.getName().equals(pName) && pd.getPropertyType().getName().equals(javaType)) {
                    Method writeMethod = pd.getWriteMethod();
                    writeMethod.invoke(instance, PropertyParser.parse(value, javaType));
                    break;
                }
            }
        }
    }

    /**
     * 获取实例
     */
    private static Object createInstance(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getTypeClazz();
        Object instance = null;
        if (beanDefinition.isSingleton()) {
            instance = singletonContainer.getInstance(clazz);
        }
        if (instance == null) {
            try {
                // 此处如果是单例模式,有创建多个类的风险，如果要保证安全，应当用一个容器来存 单例的实例
                instance = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (beanDefinition.isSingleton()) {
            singletonContainer.addInstance(clazz, instance);
        }
        return instance;
    }
}
