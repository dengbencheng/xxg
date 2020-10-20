package com.dbc.framework.contanier;

import com.dbc.framework.annotation.XxgComponent;
import com.dbc.framework.pojo.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther dbc
 * @Date 2020/10/20 14:32
 * @Description 依赖类 的容器
 */
public class ComponentContainer {

    private static Map<Class, BeanDefinition> componentMap = new ConcurrentHashMap<>(); // component类容器
    private static volatile ComponentContainer instance = null;

    private ComponentContainer() {

    }

    public static ComponentContainer getInstance() {
        if(instance == null) {
            synchronized(ComponentContainer.class) {
                instance = new ComponentContainer();
            }
        }

        return instance;
    }

    /**
     * 获取
     */
    public BeanDefinition getComponentDefinition(Class bean) {
        return componentMap.get(bean);
    }

    /**
     * 将Component类添加到容器中
     */
    public void addComponent(Class clazz) throws Exception {
        Class beanClass = clazz.getInterfaces()[0];
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setAnnotation(clazz.getAnnotation(XxgComponent.class));
        beanDefinition.setTypeClazz(clazz);
        beanDefinition.setTypeName(clazz.getName());
        if (componentMap.get(beanClass) != null) {
            throw new Exception("已存在相同的实现类" + beanClass.getName());
        }
        componentMap.put(beanClass, beanDefinition);
    }
}
