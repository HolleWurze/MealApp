package app.mealapp2.Controllers;

import app.mealapp2.Constants;
import app.mealapp2.Managers.AdminManager;
import app.mealapp2.Storage.CateringDataStore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class AdminScreenController {

    private Properties propertiesFiles = new Properties();
    private Map<String, String> cateringImageMap = new HashMap<>();
    private String propertiesFilePath;
    private String oldCatering1Value;
    private String oldCatering2Value;
    private String oldCatering3Value;
    private boolean deleteMode = false;

    @FXML
    private Button cleanButton, deleteButton;
    @FXML
    private CheckBox checkBoxCatering1, checkBoxCatering2, checkBoxCatering3;
    @FXML
    public TextField Catering1;
    @FXML
    public TextField Catering2;
    @FXML
    public TextField Catering3;
    @FXML
    public Label pathLabel1;
    @FXML
    public Label pathLabel2;
    @FXML
    public Label pathLabel3;
    @FXML
    private Label appFolderPathLabel;
    @FXML
    private Label serverFolderPathLabel;
    @FXML
    public Button chooseServerFolderButton;
    @FXML
    public Button chooseAppFolderButton;
    @FXML
    public Button goToMainMenuButton;

    @FXML
    public void initialize() {

        oldCatering1Value = Catering1.getText();
        oldCatering2Value = Catering2.getText();
        oldCatering3Value = Catering3.getText();

        AdminManager adminManager = new AdminManager();
        String appPath = Constants.APP_FOLDER_PATH;
        String serverPath = Constants.SERVER_FOLDER_PATH;

        propertiesFilePath = appPath + File.separator + "cateringFiles.properties";

        if (appPath != null && !appPath.isEmpty()) {
            appFolderPathLabel.setText(appPath);
        }

        if (serverPath != null && !serverPath.isEmpty()) {
            serverFolderPathLabel.setText(serverPath);
        }

        try {
            InputStream inputStream = new FileInputStream(propertiesFilePath);
            propertiesFiles.load(inputStream);

            Catering1.setText(propertiesFiles.getProperty("catering1.name"));
            pathLabel1.setText(propertiesFiles.getProperty("catering1.image"));

            Catering2.setText(propertiesFiles.getProperty("catering2.name"));
            pathLabel2.setText(propertiesFiles.getProperty("catering2.image"));

            Catering3.setText(propertiesFiles.getProperty("catering3.name"));
            pathLabel3.setText(propertiesFiles.getProperty("catering3.image"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        Catering1.focusedProperty().addListener((observable, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                checkForChanges(oldCatering1Value, Catering1.getText(), pathLabel1);
                oldCatering1Value = Catering1.getText();
            }
        });

        Catering2.focusedProperty().addListener((observable, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                checkForChanges(oldCatering2Value, Catering2.getText(), pathLabel2);
                oldCatering2Value = Catering2.getText();
            }
        });

        Catering3.focusedProperty().addListener((observable, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                checkForChanges(oldCatering3Value, Catering3.getText(), pathLabel3);
                oldCatering3Value = Catering3.getText();
            }
        });

        Catering1.setText(propertiesFiles.getProperty("catering1.name"));
        cateringImageMap.put(Catering1.getText(), propertiesFiles.getProperty("catering1.image"));
        Catering2.setText(propertiesFiles.getProperty("catering2.name"));
        cateringImageMap.put(Catering2.getText(), propertiesFiles.getProperty("catering2.image"));
        Catering3.setText(propertiesFiles.getProperty("catering3.name"));
        cateringImageMap.put(Catering3.getText(), propertiesFiles.getProperty("catering3.image"));
    }

    @FXML
    private void chooseAppFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            AdminManager adminManager = new AdminManager();
            adminManager.setAppDirectory(selectedDirectory.getAbsolutePath());
            appFolderPathLabel.setText(selectedDirectory.getAbsolutePath());
        } else {
            showAlert("You need to select an App directory.");
        }
    }

    @FXML
    private void chooseServerFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            AdminManager adminManager = new AdminManager();
            adminManager.setServerDirectory(selectedDirectory.getAbsolutePath());
            serverFolderPathLabel.setText(selectedDirectory.getAbsolutePath());
        } else {
            showAlert("You need to select a Server directory.");
        }
    }

    @FXML
    private void chooseImage1() {
        File file = showImageFileChooser();
        if (file != null) {
            String adjustedPath = adjustPath(file.getAbsolutePath());
            pathLabel1.setText(adjustedPath);
            saveProperties();
        }
    }

    @FXML
    private void chooseImage2() {
        File file = showImageFileChooser();
        if (file != null) {
            String adjustedPath = adjustPath(file.getAbsolutePath());
            pathLabel2.setText(adjustedPath);
            saveProperties();
        }
    }

    @FXML
    private void chooseImage3() {
        File file = showImageFileChooser();
        if (file != null) {
            String adjustedPath = adjustPath(file.getAbsolutePath());
            pathLabel3.setText(adjustedPath);
            saveProperties();
        }
    }

    private File showImageFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        return fileChooser.showOpenDialog(Catering1.getScene().getWindow());
    }

    @FXML
    public void toggleDeleteMode() {
        deleteMode = !deleteMode;
        deleteButton.setVisible(deleteMode);
        checkBoxCatering1.setVisible(deleteMode);
        checkBoxCatering2.setVisible(deleteMode);
        checkBoxCatering3.setVisible(deleteMode);
    }

    @FXML
    public void deleteSelected() {
        if (checkBoxCatering1.isSelected()) {
            propertiesFiles.remove("catering1.name");
            propertiesFiles.remove("catering1.image");
            Catering1.setText("");
            pathLabel1.setText("");
        }
        if (checkBoxCatering2.isSelected()) {
            propertiesFiles.remove("catering2.name");
            propertiesFiles.remove("catering2.image");
            Catering2.setText("");
            pathLabel2.setText("");
        }
        if (checkBoxCatering3.isSelected()) {
            propertiesFiles.remove("catering3.name");
            propertiesFiles.remove("catering3.image");
            Catering3.setText("");
            pathLabel3.setText("");
        }

        savePropertiesAfterDelete(); // сохраняем обновленные свойства

        // Закрыть режим удаления и обновить UI
        toggleDeleteMode();
    }

    private void savePropertiesAfterDelete() {
        try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
            System.out.println("Saving to: " + propertiesFilePath);  // Отладочный вывод
            propertiesFiles.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void saveProperties() {
        cateringImageMap.put(Catering1.getText(), pathLabel1.getText());
        cateringImageMap.put(Catering2.getText(), pathLabel2.getText());
        cateringImageMap.put(Catering3.getText(), pathLabel3.getText());

        CateringDataStore.saveProperties();
    }

    private void checkForChanges(String oldName, String newName, Label pathLabel) {
        if (!oldName.equals(newName)) {
            // Имя кейтеринга было изменено
            Optional<ButtonType> result = showAlert("Catering change?", "The name of the catering has been changed. Do you want to update the linked image?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Пользователь хочет обновить изображение
                File file = showImageFileChooser();
                if (file != null) {
                    pathLabel.setText(file.getAbsolutePath());
                }
            } else {
                // Перепривязка старого изображения к новому имени
                cateringImageMap.put(newName, cateringImageMap.get(oldName));
                cateringImageMap.remove(oldName);
            }
            CateringDataStore.setImagePathForCatering(newName, pathLabel.getText());
        }
    }

    private Optional<ButtonType> showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String adjustPath(String inputPath) {
        if (inputPath.startsWith("Y:\\")) {
            return inputPath.replace("Y:\\", "\\\\192.168.10.10\\Priority_int\\");
        }
        return inputPath;
    }

    @FXML
    private void goToMainMenu() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Start.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) goToMainMenuButton.getScene().getWindow();
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }
}
