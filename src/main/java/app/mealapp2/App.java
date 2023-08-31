package app.mealapp2;

import java.io.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import java.util.Properties;

@Log4j2
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Meal Ordering App");
        primaryStage.setMaximized(true);
        FXMLLoader startLoader = new FXMLLoader(getClass().getResource("/Start.fxml"));
        Parent root = startLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        String directoryPath = "C:\\MealApp";
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            if (directory.mkdir()) {
                log.info("Dir: C:\\MealApp make success");
            } else {
                log.error("Failed to create directory: C:\\MealApp");
            }
        } else {
            log.info("Directory already exist: C:\\MealApp");
        }

        try {
            copyConfigFiles(directoryPath);
        } catch (IOException e) {
            log.error("Failed to copy config files", e);
        }

        launch(args);
    }

    private static void copyConfigFiles(String directoryPath) throws IOException {
        File cateringFile = new File(directoryPath + "\\cateringFiles.properties");
        File configFile = new File(directoryPath + "\\config.properties");

        if (!cateringFile.exists() || isFileEmptyOrInvalid(cateringFile)) {
            log.info("Copying or updating cateringFiles.properties...");
            copyResourceToFile("/cateringFiles.properties", cateringFile);
        }
        if (!configFile.exists() || isFileEmptyOrInvalid(configFile)) {
            log.info("Copying or updating config.properties...");
            copyResourceToFile("/config.properties", configFile);
        }
    }

    private static boolean isFileEmptyOrInvalid(File file) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException e) {
            log.error("Failed to load properties file: " + file.getAbsolutePath(), e);
            return true; // Считаем файл недействительным
        }

        if (properties.isEmpty()) {
            return true; // файл считается недействительным
        }
        return false; // файл считается действительным
    }

    private static void copyResourceToFile(String resourceName, File outputFile) throws IOException {
        try (InputStream in = App.class.getResourceAsStream(resourceName);
             OutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}

