package com.dbc.framework.core.manager;

import com.dbc.framework.annotation.XxgComponent;
import com.dbc.framework.annotation.XxgMapper;
import com.dbc.framework.contanier.ComponentContainer;
import com.dbc.framework.contanier.MapperContainer;
import com.dbc.framework.contanier.RequestPathContainer;
import com.dbc.framework.core.io.ClassPathResource;
import com.dbc.framework.core.io.Resource;
import com.dbc.framework.factory.AutoWiredFactory;
import com.dbc.framework.factory.XmlComponentReader;
import com.dbc.framework.pojo.BeanDefinition;

import java.util.List;

/**
 * @Auther dbc
 * @Date 2020/10/21 15:16
 * @Description
 */
public class XxgAutoWiredManager extends ScannerSupport {
    private RequestPathContainer requestPathContainer = RequestPathContainer.getInstance(); // mapper类容器
    private ComponentContainer componentContainer = ComponentContainer.getInstance(); // component类容器
    private MapperContainer mapperContainer = MapperContainer.getInstance(); // mapper类容器

    public XxgAutoWiredManager(Resource resource) {
        XmlComponentReader.loadBeanToComponent(resource); // 读取配置文件中的bean到容器
    }

    @Override
    void dealClass(Class clazz) throws Exception {
        if (clazz.isAnnotationPresent(XxgComponent.class)) {
            // 将实现类添加到容器中
            componentContainer.addBeanDefinition(clazz);
            return ;
        }
        if (clazz.isAnnotationPresent(XxgMapper.class)) {
            // 将Mapper接口添加到容器中
            mapperContainer.addMapper(clazz);
        }
    }

    /**
     * 注册Component类,给@AutoWired的属性赋值
     */
    public void registerControllerAutoWired() {
        List<BeanDefinition> requestList = requestPathContainer.getRequestList();
        for (BeanDefinition beanDefinition : requestList) {
            Object instance = AutoWiredFactory.getDiInstance(beanDefinition);
            beanDefinition.getMethodDefinitions().forEach(item -> {
                item.setParentInstance(instance);
            });
        }
    }
}
