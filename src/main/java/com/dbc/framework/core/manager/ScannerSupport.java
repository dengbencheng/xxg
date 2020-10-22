package com.dbc.framework.core.manager;

import com.dbc.framework.contanier.RequestPathContainer;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

/**
 * @Auther dbc
 * @Date 2020/10/20 16:31
 * @Description 包扫描的模板方法
 */
public abstract class ScannerSupport {

    /**
     * 扫描包的方法
     */
    public final void scannerClass(String packetUrl) throws Exception{
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
                scannerClass(packetUrl + "." + f.getName());
            }
            if (f.isFile() && f.getName().endsWith(".class")) {
                String classname = f.getName().replace(".class", ""); // 去掉.class后缀名
                Class clazz = Class.forName(packetUrl + "." + classname);
                dealClass(clazz);
            }
        }
    }

     abstract void dealClass(Class clazz) throws Exception;
}
