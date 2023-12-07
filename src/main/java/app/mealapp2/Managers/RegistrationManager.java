package app.mealapp2.Managers;

import app.mealapp2.Constants;
import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class RegistrationManager {

    public boolean checkCustomerFileExists(String name, String surname) {
        Path customerFilePath = Paths.get(Constants.APP_FOLDER_PATH, name + "_" + surname + ".txt");
        return Files.exists(customerFilePath);
    }

    public void createUser(String name, String surname) throws IOException {

        // Проверка наличия папки
        File directory = new File(Constants.APP_FOLDER_PATH);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Could not create directory!");
                alert.showAndWait();
                throw new IOException("Could not create directory!");
            }
        }

        if (!checkCustomerFileExists(name, surname)) {
            String filePath = Constants.APP_FOLDER_PATH + "/" + name + "_" + surname + ".txt";
            createCustomerFile(filePath, name, surname);
        } else {
            throw new IOException("User already exists");
        }
    }


    private void createCustomerFile(String filePath, String name, String surname) throws IOException {
        Path path = Paths.get(filePath);
        Files.createFile(path);
        Files.write(path, (name + "_" + surname).getBytes());
    }

    public void appendUserToServerFile(String name, String surname) throws IOException {
        Path dirPath = Paths.get(Constants.SERVER_FOLDER_PATH, "Registration");

        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Path filePath = dirPath.resolve("AllRegistrationUsers.txt");
        String user = name + "_" + surname;

        if (!checkUserOnServer(name, surname)) {
            String content = "";
            if (Files.exists(filePath)) {
                content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                if (!content.isEmpty() && !content.endsWith(System.lineSeparator())) {
                    writer.newLine();
                }
                writer.write(user);
            }
        }
    }

    public boolean checkUserOnServer(String name, String surname) throws IOException {
        Path serverFilePath = Paths.get(Constants.SERVER_FOLDER_PATH, "/Registration/AllRegistrationUsers.txt");
        if (!Files.exists(serverFilePath)) {
            return false;
        }

        String userDetail = name + "_" + surname;
        List<String> allUsers = Files.readAllLines(serverFilePath);

        return allUsers.stream()
                .map(s -> s.replaceAll("[^\\p{L}0-9_]", "").trim())
                .anyMatch(s -> s.equalsIgnoreCase(userDetail));
    }
}
