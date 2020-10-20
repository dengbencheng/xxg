package com.dbc.framework.servlet;

import com.dbc.framework.contanier.RequestPathContainer;
import com.dbc.framework.exception.ParamException;
import com.dbc.framework.pojo.MethodDefinition;
import com.dbc.framework.pojo.ParameterDefinition;
import com.dbc.framework.utils.DateConverter;
import com.dbc.framework.utils.MyJavaType;
import com.dbc.framework.utils.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther dbc
 * @Date 2020/9/28 15:30
 */
public class DispatcherServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 编码设置
        resp.setContentType("text/json;charset=utf-8");
        RequestPathContainer requestPathContainer = RequestPathContainer.getInstance();
        MethodDefinition methodDefinition = requestPathContainer.getMethodDefinition(req.getRequestURI(), req.getMethod());

        if (methodDefinition == null) {
            resp.setStatus(404);
            sendResponse(R.failed("请求路径不存在"), req, resp);
            return;
        }

        List<ParameterDefinition> parameterDefinitions = methodDefinition.getParameterDefinitions();
        List<Object> params = new ArrayList<>(parameterDefinitions.size());
        for (ParameterDefinition parameterDefinition : parameterDefinitions) {
            try {
                Object value = dealParam(parameterDefinition, req, resp);
                params.add(value);
            } catch (ParamException e) {
                resp.setStatus(404);
                sendResponse(R.failed(e.getMessage()), req, resp);
                return ;
            }
        }

        try {
            Object result = methodDefinition.getMethod().invoke(
                    methodDefinition.getParentInstance(),
                    params.toArray());
            sendResponse(result, req, resp);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            sendResponse(e.getMessage(), req, resp);
        }

    }

    /**
     * 处理参数
     * @param parameterDefinition
     * @param req
     * @param resp
     */
    private Object dealParam(ParameterDefinition parameterDefinition, HttpServletRequest req, HttpServletResponse resp) throws ParamException, IOException {
        Object value;
        String data = "";
        if (parameterDefinition.isRequestBody()) {
            // 从请求体(request的输入流)中获取数据
            data = getJsonString(req);
        } else if (parameterDefinition.getParamType() == HttpServletRequest.class) {
            return req;
        } else if (parameterDefinition.getParamType() == HttpServletResponse.class) {
            return resp;
        } else if (isJavaType(parameterDefinition)) {
            // 从url中取出参数
            data = req.getParameter(parameterDefinition.getParamName());
            if(data == null) {
                throw new ParamException("服务器无法拿到请求数据,请检查请求头等");
            }
        } else {
            // 将请求url中的参数封装成对象
            try {
                Object obj = parameterDefinition.getParamClazz().newInstance();
                ConvertUtils.register(new DateConverter(), Date.class);
                BeanUtils.populate(obj, req.getParameterMap());
                return obj;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ParamException("未找到参数'" + parameterDefinition.getParamName() + "'对应的值");
            }
        }
        try {
            value = objectMapper.readValue(data, parameterDefinition.getParamClazz());
        } catch (JsonProcessingException e) {
            String errMsg = "参数'" + parameterDefinition.getParamName() +
                    "'需要'" + parameterDefinition.getParamType() +
                    "类型";
            throw new ParamException(errMsg);
        }
        return value;
    }

    private void sendResponse(Object result, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (result == null) {
            return;
        }
        resp.setContentType("text/json;charset=utf-8");
        objectMapper.writeValue(resp.getWriter(), result);
    }

    /**
     * 判断参数是否是普通类型
     * @return
     */
    private boolean isJavaType(ParameterDefinition parameterDefinition) {
        Object[] javaTypes = MyJavaType.getJavaTypes();
        for (Object item : javaTypes) {
            if (item.equals(parameterDefinition.getParamClazz())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取请求头的json字符串
     */
    private String getJsonString(HttpServletRequest req) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "utf-8"));
        char[] chars = new char[1024];
        int len;
        StringBuilder sb = new StringBuilder();
        while ((len = br.read(chars)) != -1) {
            sb.append(chars, 0, len);
        }
        return sb.toString();
    }

}
