package com.dbc.framework.core.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Auther dbc
 * @Date 2020/10/20 19:06
 * @Description
 */
public class FileSystemResource implements Resource {
    private String fileName;

    public FileSystemResource(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(fileName);
    }
}
