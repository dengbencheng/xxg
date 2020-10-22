package com.dbc.framework.contanier;

import com.dbc.framework.annotation.XxgComponent;
import com.dbc.framework.exception.ComponentException;
import com.dbc.framework.pojo.BeanDefinition;
import com.dbc.framework.pojo.PropertyValue;
import com.dbc.framework.pojo.RuntimeBeanReference;
import com.dbc.framework.pojo.TypeStringValue;
import org.dom4j.Element;

import java.util.Iterator;
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
        if (instance != null) {
            throw new RuntimeException("单例模式");
        }
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
    public BeanDefinition getBeanDefinition(Class bean) {
        return componentMap.get(bean);
    }

    /**
     * 将BeanDefinition类添加到容器中
     */
    public void addBeanDefinition(Class implClazz) throws ComponentException {
        Class beanClass = implClazz.getInterfaces()[0];
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setAnnotation(implClazz.getAnnotation(XxgComponent.class));
        beanDefinition.setTypeClazz(implClazz);
        beanDefinition.setTypeName(implClazz.getName());
        if (componentMap.get(beanClass) != null) {
            throw new ComponentException(beanClass.getName() + "已存在相同的实现类");
        }
        componentMap.put(beanClass, beanDefinition);
    }

    /**
     * 将BeanDefinition类添加到容器中
     */
    public void addBeanDefinition(Element beanEle) throws ClassNotFoundException {

        String id = beanEle.attributeValue("id");
        String className = beanEle.attributeValue("class");
        String scope = beanEle.attributeValue("scope");
        Class implClazz = Class.forName(className);
        Class beanClass = Class.forName(id);
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setTypeClazz(implClazz);
        beanDefinition.setXmlDependency(true);
        if (scope == null || "singleton".equals(scope)) {
            beanDefinition.setSingleton(true);
        } else {
            beanDefinition.setSingleton(false);
        }
        loadProperty(beanDefinition, beanEle);
        beanDefinition.setTypeName(implClazz.getName());
        if (componentMap.get(beanClass) != null) {
            throw new ComponentException(beanClass.getName() + "已存在相同的实现类");
        }
        componentMap.put(beanClass, beanDefinition);
    }

    private void loadProperty(BeanDefinition beanDefinition, Element beanEle) {
        Iterator<Element> propertyIte = beanEle.elementIterator("property");
        while (propertyIte.hasNext()) {
            Element propertyEle = propertyIte.next();
            PropertyValue propertyValue = loadPropertyValue(propertyEle);
            beanDefinition.getPropertyValueList().add(propertyValue);
        }
    }

    private PropertyValue loadPropertyValue(Element propertyEle) {
        String name = propertyEle.attributeValue("name");
        String javaType = propertyEle.attributeValue("javaType");
        String ref = propertyEle.attributeValue("ref");
        String value = propertyEle.attributeValue("value");
        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setPName(name);
        propertyValue.setJavaType(javaType);
        if (ref != null) {
            propertyValue.setValue(new RuntimeBeanReference(ref));
        } else if (value != null) {
            propertyValue.setValue(new TypeStringValue(value));
        }
        return propertyValue;
    }
}
