package app.mealapp2.Managers;

import app.mealapp2.Storage.CateringDataStore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AdminManager {
    private static final String DEFAULT_CONFIG_FILE_PATH = "config.properties";
    private static final String EXTERNAL_CONFIG_FILE_PATH = "C:\\MealApp\\config.properties";
    private Properties propertiesFolders;

    public AdminManager() {
        propertiesFolders = new Properties();

        // Пытаемся загрузить из внешней папки
        try (InputStream externalInputStream = new FileInputStream(EXTERNAL_CONFIG_FILE_PATH)) {
            propertiesFolders.load(externalInputStream);
        } catch (IOException e) {
            // Не удалось загрузить из внешней папки. Загрузим из classpath.
            try (InputStream defaultInputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_PATH)) {
                if (defaultInputStream != null) {
                    propertiesFolders.load(defaultInputStream);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        setDefaultPropertiesIfAbsent();
        saveProperties();
    }

    private void setDefaultPropertiesIfAbsent() {
        if (!propertiesFolders.containsKey("appSelectedDirectory")) {
            propertiesFolders.setProperty("appSelectedDirectory", "C:\\MealApp");
        }
        if (!propertiesFolders.containsKey("serverSelectedDirectory")) {
            propertiesFolders.setProperty("serverSelectedDirectory", "\\\\192.168.10.10\\Priority_int\\MealApp");
        }
    }

    public String getAppDirectory() {
        return propertiesFolders.getProperty("appSelectedDirectory", "");  // Второй параметр - значение по умолчанию.
    }

    public void setAppDirectory(String path) {
        propertiesFolders.setProperty("appSelectedDirectory", path);
        saveProperties();
    }

    public String getServerDirectory() {
        return propertiesFolders.getProperty("serverSelectedDirectory", "");
    }

    public void setServerDirectory(String path) {
        propertiesFolders.setProperty("serverSelectedDirectory", path);
        saveProperties();
    }

    private void saveProperties() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(EXTERNAL_CONFIG_FILE_PATH);
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

