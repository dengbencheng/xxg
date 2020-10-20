package com.dbc.framework.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述参数的类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDefinition {
    private Class paramClazz; // 参数类对象
    private String paramName; // 参数名称
    private Object paramType; // 参数类型
    private boolean isRequestBody; // 是否是获取body中数据
}
