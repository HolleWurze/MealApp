package app.mealapp2.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminLoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Aa@123456";
    private boolean authenticated = false;

    @FXML
    private void handleLogin() {
        if (ADMIN_USERNAME.equals(usernameField.getText()) &&
                ADMIN_PASSWORD.equals(passwordField.getText())) {
            authenticated = true;
            ((Stage) usernameField.getScene().getWindow()).close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong Password");
            alert.setContentText("You entered an incorrect password.");
            alert.showAndWait();
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
