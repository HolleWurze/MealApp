package app.mealapp2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.Message;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
public class App extends Application {
    private FXTrayIcon trayIcon;
    private PopupMenu popup;
    private MenuItem openItem;
    private MenuItem closeItem;
    private SystemTray tray;

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Meal Ordering App");
        FXMLLoader startLoader = new FXMLLoader(getClass().getResource("/Start.fxml"));
        Parent root = startLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        FXTrayIcon trayIcon = new FXTrayIcon(primaryStage, getClass().getResource("/ico.png"));
        trayIcon.show();

        javafx.scene.control.MenuItem openItem = new javafx.scene.control.MenuItem("Open");
        openItem.setOnAction(e -> primaryStage.show());
        trayIcon.addMenuItem(openItem);

        javafx.scene.control.MenuItem closeItem = new javafx.scene.control.MenuItem("Close");
        closeItem.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
        trayIcon.addMenuItem(closeItem);

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.hide();
        });

        primaryStage.show();

        scheduleNotifications(trayIcon);
    }

    private void scheduleNotifications(FXTrayIcon trayIcon) {
        Timer timer = new Timer();
        TimerTask dailyTask = new TimerTask() {
            @Override
            public void run() {
                Calendar now = Calendar.getInstance();
                int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek >= Calendar.SUNDAY && dayOfWeek <= Calendar.FRIDAY) {
                    if (SystemTray.isSupported()) {
                        trayIcon.showInfoMessage("MealApp Reminder", "Good morning!\nHave a good day!\nDon't forget to order food!");
                    }
                }
            }
        };

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();

        timer.scheduleAtFixedRate(dailyTask, time, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
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
            return true;
        }

        if (properties.isEmpty()) {
            return true;
        }
        return false;
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

