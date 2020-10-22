package com.dbc.framework.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Auther dbc
 * @Date 2020/10/20 19:05
 * @Description
 */
public class ClassPathResource implements Resource {

    private String fileName;

    public ClassPathResource(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
