package com.dbc.framework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Auther dbc
 * @Date 2020/10/20 19:05
 * @Description
 */
public interface Resource {
    InputStream getInputStream() throws IOException;
}
