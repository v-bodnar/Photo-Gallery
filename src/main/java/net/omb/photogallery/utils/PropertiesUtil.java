package net.omb.photogallery.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static final String configPath = "application.properties";
    private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties properties;

    public static Object getProperty(String key) {
        try {
            if(properties == null){
                properties = new Properties();
                InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(configPath);
                if (in == null || in.available() == 0) {
                    new FileNotFoundException();
                }
                properties.load(in);
                in.close();
            }
            return properties.getProperty(key);
        } catch (java.io.IOException e) {
            log.error("Error during config read!", e);
            return null;
        }
    }
}
