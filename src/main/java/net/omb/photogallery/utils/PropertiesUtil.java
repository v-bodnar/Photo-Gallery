package net.omb.photogallery.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class PropertiesUtil {
    private static final String configPath = "application.properties";
    private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties properties;

    public static Object getProperty(String key) {
        try {
            if(properties == null){
                File propertiesFile  = new File(configPath);

                if (!propertiesFile.exists() || !propertiesFile.canRead()) {
                    new FileNotFoundException();
                }

                properties = new Properties();
                FileInputStream in = new FileInputStream(propertiesFile);
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
