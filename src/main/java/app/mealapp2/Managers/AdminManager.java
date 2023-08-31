package app.mealapp2.Managers;

import app.mealapp2.Storage.CateringDataStore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AdminManager {
    private static final String CONFIG_FILE_PATH = "config.properties";
    private Properties propertiesFolders;
    private static InputStream inputStream = CateringDataStore.class.getClassLoader().getResourceAsStream("config.properties");

    public AdminManager() {
        propertiesFolders = new Properties();
        try {
            propertiesFolders.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!propertiesFolders.containsKey("appSelectedDirectory")) {
            propertiesFolders.setProperty("appSelectedDirectory", "C:\\MealApp");
        }
        if(!propertiesFolders.containsKey("serverSelectedDirectory")) {
            propertiesFolders.setProperty("serverSelectedDirectory", "\\\\192.168.10.10\\Priority_int\\MealApp");
        }
        saveProperties();
    }

    public String getAppDirectory() {
        return propertiesFolders.getProperty("appSelectedDirectory", "");  // Второй параметр - значение по умолчанию.
    }

    public void setAppDirectory(String path) {
        propertiesFolders.setProperty("appSelectedDirectory", path);
        saveProperties();
    }

    public String getServerDirectory() {
        return propertiesFolders.getProperty("serverSelectedDirectory", "");  // Второй параметр - значение по умолчанию.
    }

    public void setServerDirectory(String path) {
        propertiesFolders.setProperty("serverSelectedDirectory", path);
        saveProperties();
    }

    private void saveProperties() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(CONFIG_FILE_PATH);
            propertiesFolders.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

