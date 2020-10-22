package com.dbc.framework.factory;

import com.dbc.framework.contanier.ComponentContainer;
import com.dbc.framework.pojo.RuntimeBeanReference;
import com.dbc.framework.pojo.TypeStringValue;

/**
 * @Auther dbc
 * @Date 2020/10/22 10:27
 * @Description
 */
public class PropertyParser {
    private static ComponentContainer componentContainer = ComponentContainer.getInstance(); // component类容器

    public static Object parse(Object value, String javaType) throws ClassNotFoundException {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference runtimeBeanReference = (RuntimeBeanReference) value;
            Class implClazz = Class.forName(runtimeBeanReference.getValue());
            return AutoWiredFactory.getDiInstance(componentContainer.getBeanDefinition(implClazz));
        } else if (value instanceof TypeStringValue) {
            TypeStringValue typeStringValue = (TypeStringValue) value;
            return parseJavaType(typeStringValue.getValue(), javaType);
        }
        return null;
    }

    private static Object parseJavaType(String value, String javaType) {
        Object parsedValue = null;
        switch (javaType) {
            case "java.lang.String":
                parsedValue = value;
                break;
            case "int":
            case "java.lang.Integer":
                parsedValue = Integer.parseInt(value);
                break;
            case "float":
            case "java.lang.Float":
                parsedValue = Float.parseFloat(value);
                break;
            case "double":
            case "java.lang.Double":
                parsedValue = Double.parseDouble(value);
                break;
            default:
                parsedValue = value;
        }
        return parsedValue;
    }
}
