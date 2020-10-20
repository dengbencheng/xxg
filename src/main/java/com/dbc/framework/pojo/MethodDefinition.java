package com.dbc.framework.pojo;

import com.dbc.framework.annotation.XxgRequestMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 描述方法的类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodDefinition {
    private Class parentClazz; // 所属父类的class
    private Object parentInstance; // 所属父类的实例
    private Method method; // 方法
    private String methodName; // 方法名
    private Object annotation; // 注解类
    private String requestMappingUrlPath; // url
    private String[] allowedRequestMethods; // allowedRequestMethods
    private List<ParameterDefinition> parameterDefinitions;  // 参数列表
    private Object result;  // 返回数据
}
