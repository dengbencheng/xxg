package com.dbc.framework.core.manager;

import com.dbc.framework.annotation.*;
import com.dbc.framework.contanier.ComponentContainer;
import com.dbc.framework.contanier.MapperContainer;
import com.dbc.framework.contanier.RequestPathContainer;
import com.dbc.framework.factory.AutoWiredFactory;
import com.dbc.framework.pojo.BeanDefinition;
import com.dbc.framework.pojo.MethodDefinition;
import com.dbc.framework.pojo.ParameterDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther dbc
 * @Date 2020/10/20 14:36
 * @Description 框架默认的管理器 实现功能如下: 装载容器、注册AutoWired
 */
public class XxgControllerManager extends ScannerSupport {
    private RequestPathContainer requestPathContainer = RequestPathContainer.getInstance(); // mapper类容器

    public XxgControllerManager() {

    }

    // 筛选包中的类,并添加到List中
    public void dealClass(Class clazz) throws Exception {
        if (!clazz.isAnnotationPresent(XxgController.class)) {
            // 没有controller注解
            return;
        }
        List<MethodDefinition> methodDefinitions = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // 方法转 方法描述类
            MethodDefinition methodDefinition = convertMethodToMethodDefinition(method, clazz);
            if (methodDefinition != null) {
                methodDefinitions.add(methodDefinition);
            }
        }
        if (methodDefinitions.size() == 0) {
            return;
        }
        // 设置类描述类
        BeanDefinition beanDefinition = convertBeanToBeanDefinition(clazz, methodDefinitions);
        requestPathContainer.addBeanDefinition(beanDefinition);
    }

    /**
     * 将controller类 转换为 类的描述类
     */
    private BeanDefinition convertBeanToBeanDefinition(Class clazz, List<MethodDefinition> methodDefinitions) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setTypeName(clazz.getName());
        beanDefinition.setTypeClazz(clazz);
        XxgController controller = (XxgController) clazz.getAnnotation(XxgController.class);
        beanDefinition.setAnnotation(controller);
        beanDefinition.setControllerUrlPath(controller.value());
        beanDefinition.setMethodDefinitions(methodDefinitions);// 增加方法体
        return beanDefinition;
    }

    /**
     * 将方法 转换为 方法描述类
     */
    private MethodDefinition convertMethodToMethodDefinition(Method method, Class clazz) {
        if (!method.isAnnotationPresent(XxgRequestMapping.class)) {
            // 没有RequestMapping注解
            return null;
        }
        method.setAccessible(true);
        Parameter[] parameters = method.getParameters();
        // 设置参数描述类
        List<ParameterDefinition> parameterDefinitions = new ArrayList<>();
        for ( Parameter parameter : parameters) {
            ParameterDefinition parameterDefinition = convertParamToParameterDefinition(parameter);
            parameterDefinitions.add(parameterDefinition);
        }
        // 设置方法描述类
        MethodDefinition methodDefinition = new MethodDefinition();
        methodDefinition.setParameterDefinitions(parameterDefinitions);  // 增加参数列表
        methodDefinition.setMethod(method);
        methodDefinition.setMethodName(method.getName());
        methodDefinition.setResult(method.getReturnType());
        XxgRequestMapping requestMapping = method.getAnnotation(XxgRequestMapping.class);
        methodDefinition.setRequestMappingUrlPath(requestMapping.value());
        methodDefinition.setAnnotation(requestMapping);
        methodDefinition.setAllowedRequestMethods(requestMapping.methods());
        methodDefinition.setParentClazz(clazz);
        return methodDefinition;
    }

    /**
     * 将参数 转换为 参数描述类
     */
    private ParameterDefinition convertParamToParameterDefinition(Parameter parameter) {
        ParameterDefinition parameterDefinition = new ParameterDefinition();
        if (parameter.isAnnotationPresent(XxgParam.class)) {
            parameterDefinition.setParamName(parameter.getAnnotation(XxgParam.class).value());
        } else {
            parameterDefinition.setParamName(parameter.getName());
        }
        parameterDefinition.setParamClazz(parameter.getType());
        parameterDefinition.setParamType(parameter.getType());
        parameterDefinition.setRequestBody(parameter.isAnnotationPresent(XxgRequestBody.class));
        return parameterDefinition;
    }



}
