package org.company.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class FileUtils {

  private static Properties serviceConfig;

  static {
    serviceConfig = new Properties();
    var propertiesFileInpStream =
        FileUtils.class.getClassLoader().getResourceAsStream("service-config.properties");
    try {
      serviceConfig.load(propertiesFileInpStream);
      LogUtils.debug("Service config loaded successfully");
    } catch (IOException e) {
      LogUtils.error("Unable to read service config", e);
    }
  }

  public static String getPropertyValue(String propertyKey) {
    return serviceConfig.getProperty(propertyKey, StringUtils.EMPTY);
  }

  public static String getPropertyValue(String propertyFileName, String propertyKey) {
    Properties properties = loadProperties(propertyFileName);
    return properties.getProperty(propertyKey, StringUtils.EMPTY);
  }

  public static String getPropertyValue(Properties properties, String propertyKey) {
    return properties.getProperty(propertyKey, StringUtils.EMPTY);
  }

  public static Properties loadProperties(String propertyFileName) {
    var properties = new Properties();
    var propertiesFileInpStream =
        FileUtils.class.getClassLoader().getResourceAsStream(propertyFileName);
    try {
      properties.load(propertiesFileInpStream);
      LogUtils.debug("{} loaded successfully", properties);
    } catch (IOException e) {
      LogUtils.error(String.format("Unable to read %s", properties), e);
    }
    return properties;
  }

  public static String readFile(String path, String fileName) {
    LogUtils.debug("Reading from file: {}/{}", path, fileName);
    try {
      var fullFilePath = String.format("%s/%s", path, fileName);
      var fileContent = Files.readString(Paths.get(System.getProperty("user.dir"), path, fileName));
      LogUtils.trace("File content", fileContent);
      return fileContent;
    } catch (IOException e) {
      LogUtils.error(String.format("Unable to read %s", fileName), e);
    }
    return StringUtils.EMPTY;
  }

  public static JSONObject readFileAsJsonObject(String path, String fileName) {
    return new JSONObject(readFile(path, fileName));
  }
}
