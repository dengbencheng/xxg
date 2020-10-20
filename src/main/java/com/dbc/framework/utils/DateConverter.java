package com.dbc.framework.utils;

import org.apache.commons.beanutils.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther dbc
 * @Date 2020/9/29 14:24
 */
public class DateConverter implements Converter {
    @Override
    public Object convert(Class aClass, Object o) {
        if (o == null) {
            return null;
        }
        if(!(o instanceof String)) {
            return o;
        }
        String value = (String) o;
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
