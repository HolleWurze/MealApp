package app.mealapp2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.Properties;

public final class Constants {
    private static final String CONFIG_FILE_PATH = "config.properties";
    private static final Properties properties = new Properties();
    public static final String SERVER_FOLDER_PATH;
    public static final String APP_FOLDER_PATH;

    static {
        // Загружаем свойства по умолчанию на случай, если чтение файла не удастся
        String serverFolderPathDefault = "\\\\192.168.10.10\\Priority_int\\MealApp";
        String appFolderPathDefault = "C:\\MealApp";

        // Загрузка из внутреннего ресурса
        try {
            InputStream internalStream = Constants.class.getClassLoader().getResourceAsStream(CONFIG_FILE_PATH);
            if (internalStream != null) {
                properties.load(internalStream);
                serverFolderPathDefault = properties.getProperty("serverSelectedDirectory", serverFolderPathDefault);
                appFolderPathDefault = properties.getProperty("appSelectedDirectory", appFolderPathDefault);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Загрузка из внешнего файла
        File externalFile = new File(appFolderPathDefault + File.separator + CONFIG_FILE_PATH);
        if (externalFile.exists()) {
            try {
                properties.load(new FileInputStream(externalFile));
                serverFolderPathDefault = properties.getProperty("serverSelectedDirectory", serverFolderPathDefault);
                appFolderPathDefault = properties.getProperty("appSelectedDirectory", appFolderPathDefault);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SERVER_FOLDER_PATH = serverFolderPathDefault;
        APP_FOLDER_PATH = appFolderPathDefault;
    }
    // Запрет создания экземпляров класса Constants
    private Constants() {
        throw new AssertionError("Cannot create instance of a Constants class");
    }
}