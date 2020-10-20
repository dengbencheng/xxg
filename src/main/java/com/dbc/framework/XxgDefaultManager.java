package com.dbc.framework;

import com.dbc.framework.annotation.*;
import com.dbc.framework.contanier.ComponentContainer;
import com.dbc.framework.contanier.MapperContainer;
import com.dbc.framework.contanier.RequestPathContainer;
import com.dbc.framework.factory.AutoWiredFactory;
import com.dbc.framework.pojo.BeanDefinition;
import com.dbc.framework.pojo.MethodDefinition;
import com.dbc.framework.pojo.ParameterDefinition;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther dbc
 * @Date 2020/10/20 14:36
 * @Description 框架默认的管理器 实现功能如下：扫描包、注册AutoWired
 */
public class XxgDefaultManager {
    private static RequestPathContainer requestPathContainer = RequestPathContainer.getInstance(); // mapper类容器
    private static ComponentContainer componentContainer = ComponentContainer.getInstance(); // component类容器
    private static MapperContainer mapperContainer = MapperContainer.getInstance(); // mapper类容器

    /**
     * 扫描包
     */
    public static void scanner(String packetUrl) throws Exception {
        String url = packetUrl.replace(".", "/");
        URL resource = RequestPathContainer.class.getClassLoader().getResource(url); // 获取绝对路径
        if (resource == null) {
            return;
        }
        String path = resource.getPath();
        File file = new File(URLDecoder.decode(path, "UTF-8"));
        if (!file.exists()) {
            return;
        }
        // 因为每次调用scanner方法,都是传的文件夹,所以file也不可能是文件
        if (!file.isDirectory()){
            return;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                scanner(packetUrl + "." + f.getName());
            }
            if (f.isFile() && f.getName().endsWith(".class")) {
                String classname = f.getName().replace(".class", ""); // 去掉.class后缀名
                Class clazz = Class.forName(packetUrl + "." + classname);
                dealClass(clazz);
            }
        }
    }

    // 筛选包中的类,并添加到List中
    private static void dealClass(Class clazz) throws Exception {
        if (clazz.isAnnotationPresent(XxgComponent.class)) {
            // 将实现类添加到容器中
            componentContainer.addComponent(clazz);
            return ;
        }
        if (clazz.isAnnotationPresent(XxgMapper.class)) {
            // 将Mapper接口添加到容器中
            mapperContainer.addMapper(clazz);
            return ;
        }
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
    private static BeanDefinition convertBeanToBeanDefinition(Class clazz, List<MethodDefinition> methodDefinitions) {
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
    private static MethodDefinition convertMethodToMethodDefinition(Method method, Class clazz) {
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
    private static ParameterDefinition convertParamToParameterDefinition(Parameter parameter) {
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

    /**
     * 注册Component类,给@AutoWired的属性赋值
     */
    public static void registerAutoWired() {
        List<BeanDefinition> requestList = requestPathContainer.getRequestList();
        for (BeanDefinition beanDefinition : requestList) {
            Object instance = AutoWiredFactory.getDiInstance(beanDefinition.getTypeClazz(), null);
            beanDefinition.getMethodDefinitions().forEach(item -> {
                item.setParentInstance(instance);
            });
        }
    }

}
