package app.mealapp2.Controllers;

import app.mealapp2.Entity.User;
import app.mealapp2.Managers.RegistrationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;

    private RegistrationManager registrationManager = new RegistrationManager();

    @FXML
    public void registerUser() {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        User user = new User(name,surname);

        if (name.isEmpty() || surname.isEmpty()) {
            showAlert("Registration Failed", "Name and Surname fields can't be empty!");
            return;
        }

        try {
            if (registrationManager.checkCustomerFileExists(name, surname)) {
                if (!registrationManager.checkUserOnServer(name, surname)) {
                    registrationManager.appendUserToServerFile(name, surname);
                }
                loadFXML("/Order.fxml", user);
            } else {
                registrationManager.createUser(name, surname);
                registrationManager.appendUserToServerFile(name, surname);
                loadFXML("/Order.fxml", user);
            }
        } catch (IOException e) {
            showAlert("Registration Failed", "Error while processing the registration!");
        }
    }

    private void loadFXML(String resourcePath, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        Parent root = loader.load();

        if (user != null && "/Order.fxml".equals(resourcePath)) {
            OrderController orderController = loader.getController();
            orderController.setUser(user);
        }

        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
