package com.dbc.framework.pojo;

import lombok.Data;

/**
 * @Auther dbc
 * @Date 2020/10/21 14:49
 * @Description
 */
@Data
public class PropertyValue {
    private String pName;
    private Object value;
    private String javaType;
}
