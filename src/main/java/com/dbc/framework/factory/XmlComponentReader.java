package com.dbc.framework.factory;

import com.dbc.framework.contanier.ComponentContainer;
import com.dbc.framework.core.io.Resource;
import com.dbc.framework.exception.ComponentException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.util.Iterator;

/**
 * @Auther dbc
 * @Date 2020/10/20 19:14
 * @Description
 */
public class XmlComponentReader {
    private static ComponentContainer componentContainer = ComponentContainer.getInstance();


    public static void loadBeanToComponent(Resource resource) {
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(resource.getInputStream());
            Iterator<Element> beanIterator = document.getRootElement().elementIterator("bean");
            while (beanIterator.hasNext()) {
                Element beanEle = beanIterator.next();
                componentContainer.addBeanDefinition(beanEle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ComponentException("xml文件读取错误", e);
        }
    }

}
