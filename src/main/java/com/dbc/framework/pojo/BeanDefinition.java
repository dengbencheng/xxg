package com.dbc.framework.pojo;

import com.dbc.framework.annotation.XxgController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用来存放controller类的相关参数、方法等
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanDefinition {
    private Class typeClazz; // 类对象
    private String typeName; // 类名
    private Object annotation; // 注解
    private String controllerUrlPath; // controller的path路径
    private List<MethodDefinition> methodDefinitions; // 带有RequestMapping的注解
}
