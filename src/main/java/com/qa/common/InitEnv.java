package com.qa.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 加载配置文件
 */
public class InitEnv {
    public static Properties properties = new Properties();

    static {
        InputStream inputStream = Object.class.getResourceAsStream("/env.properties"); //这里必须是以/开始路径
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
