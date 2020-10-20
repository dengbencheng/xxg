package com.dbc.framework.contanier;

import com.dbc.framework.annotation.XxgMapper;
import com.dbc.framework.pojo.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther dbc
 * @Date 2020/10/20 11:59
 * @Description Mapper 类的容器
 */
public class MapperContainer {
    private static Map<Class, BeanDefinition> mapperDefinitionMap = new ConcurrentHashMap<>(); // mapper接口容器
    private static volatile MapperContainer mapperFactory = null;

    private MapperContainer() {

    }

    public static MapperContainer getInstance() {
        if (mapperFactory == null) {
            synchronized (MapperContainer.class) {
                mapperFactory = new MapperContainer();
            }
        }
        return mapperFactory;
    }

    /**
     * 将Mapper接口添加到容器中
     */
    public void addMapper(Class clazz) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setTypeClazz(clazz);
        beanDefinition.setTypeName(clazz.getName());
        beanDefinition.setAnnotation(clazz.getAnnotation(XxgMapper.class));
        mapperDefinitionMap.put(clazz, beanDefinition);
    }

    /**
     * 获取
     */
    public BeanDefinition getMapperDefinition(Class clazz) {
        return mapperDefinitionMap.get(clazz);
    }

}
