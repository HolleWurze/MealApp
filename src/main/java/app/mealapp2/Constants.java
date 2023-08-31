package app.mealapp2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class Constants {
    private static final String CONFIG_FILE_PATH = "config.properties";
    public static final String SERVER_FOLDER_PATH;
    public static final String APP_FOLDER_PATH;
    private Properties properties;


    static {
        // Загружаем свойства по умолчанию на случай, если чтение файла не удастся
        String serverFolderPathDefault = "\\\\192.168.10.10\\Priority_int\\MealApp";
        String appFolderPathDefault = "C:\\MealApp";

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(CONFIG_FILE_PATH));
            serverFolderPathDefault = properties.getProperty("serverSelectedDirectory", serverFolderPathDefault);
            appFolderPathDefault = properties.getProperty("appSelectedDirectory", appFolderPathDefault);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SERVER_FOLDER_PATH = serverFolderPathDefault;
        APP_FOLDER_PATH = appFolderPathDefault;
    }
    // Запрет создания экземпляров класса Constants
    private Constants() {
        throw new AssertionError("Cannot create instance of a Constants class");
    }
}


