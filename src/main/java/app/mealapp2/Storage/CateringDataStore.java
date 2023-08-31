package app.mealapp2.Storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class CateringDataStore {

    private static final Map<String, String> cateringImagesMap = new LinkedHashMap<>();
    private static final String propertiesFilePath = "C:\\MealApp\\cateringFiles.properties";
    private static Properties properties = new Properties();

    // Статический блок для загрузки данных при запуске приложения
    static {
        try {
            properties.load(new FileInputStream(propertiesFilePath));

            for (String key : properties.stringPropertyNames()) {
                if (key.endsWith(".name")) {
                    String name = properties.getProperty(key);
                    if (name != null && !name.trim().isEmpty()) {
                        String imageKey = key.replace(".name", ".image");
                        String imagePath = properties.getProperty(imageKey);
                        if (imagePath != null && !imagePath.trim().isEmpty()) {
                            cateringImagesMap.put(name, imagePath);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getAllCateringNames() {
        return cateringImagesMap.keySet();
    }

    public static String getImagePathForCatering(String cateringName) {
        return cateringImagesMap.get(cateringName);
    }

    public static void setImagePathForCatering(String cateringName, String imagePath) {
        cateringImagesMap.put(cateringName, imagePath);
        saveProperties();
    }

    private static void saveProperties() {
        try {
            properties.load(new FileInputStream(propertiesFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, String> entry : cateringImagesMap.entrySet()) {
            String keyName = getKeyForValue(properties, entry.getValue());
            if (keyName == null) {
                continue;
            }

            String index = keyName.split("\\.")[0].replaceAll("[^0-9]", "");

            properties.setProperty("catering" + index + ".name", entry.getKey());
            properties.setProperty("catering" + index + ".image", entry.getValue());
        }

        try {
            properties.store(new FileOutputStream(propertiesFilePath), null); // Используем полный путь
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getKeyForValue(Properties properties, String value) {
        for (String key : properties.stringPropertyNames()) {
            if (value.equals(properties.get(key))) {
                return key;
            }
        }
        return null;
    }
}

