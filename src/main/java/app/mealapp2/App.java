package app.mealapp2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
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

        // Создание объекта File
        File directory = new File(directoryPath);

        // Проверка существования директории
        if (!directory.exists()) {
            if (directory.mkdir()) {
                log.info("Dir: C:\\MealApp make success");
            } else {
                log.error("Failed to create directory: C:\\MealApp");
            }
        } else {
            log.info("Directory already exist: C:\\MealApp");
        }
        launch(args);
    }
}
