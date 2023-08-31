package app.mealapp2.Controllers;

import app.mealapp2.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserListController {
    public ListView<String> usersListView;
    public Button closeButton;

    @FXML
    public void initialize() throws IOException {

        List<String> usersWhoForgot = getUsersWhoForgotToOrder();
//        if (usersWhoForgot.isEmpty()) {
//            showAlert("All users ordered today", "Great! All users who wanted ordered today.");
//        }
        ObservableList<String> observableList = FXCollections.observableArrayList(usersWhoForgot);
        usersListView.setItems(observableList);
    }

    @FXML
    private void closeButtonPressed() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public List<String> getUsersWhoForgotToOrder() throws IOException {
        List<String> allRegisteredUsers = getAllRegisteredUsers();
        List<String> usersWhoOrderedToday = getUsersWhoOrderedToday();

        allRegisteredUsers.removeAll(usersWhoOrderedToday);

        return allRegisteredUsers;
    }

    private List<String> getAllRegisteredUsers() throws IOException {
        Path serverFilePath = Paths.get(Constants.SERVER_FOLDER_PATH, "Registration", "AllRegistrationUsers.txt");

        if (!Files.exists(serverFilePath)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(serverFilePath);
    }

    private List<String> getUsersWhoOrderedToday() {
        String todayPrefix = LocalDate.now().toString();
        File folder = new File(Constants.SERVER_FOLDER_PATH + "\\Orders");
        File[] listOfFiles = folder.listFiles();

        List<String> usersWhoOrderedToday = new ArrayList<>();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().startsWith(todayPrefix)) {
                String[] filenameParts = file.getName().split("_");
                String fullName = filenameParts[2] + "_" + filenameParts[3].split("\\.")[0]; // Remove .txt
                usersWhoOrderedToday.add(fullName);
            }
        }

        return usersWhoOrderedToday;
    }
}
