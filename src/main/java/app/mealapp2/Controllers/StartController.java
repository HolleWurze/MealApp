package app.mealapp2.Controllers;

import app.mealapp2.Constants;
import app.mealapp2.Entity.User;
import app.mealapp2.Managers.RegistrationManager;
import app.mealapp2.Styles.StyleUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StartController {

    @FXML
    public Label infoLabel;
    private RegistrationManager registrationManager = new RegistrationManager();
    @FXML
    private Button orderButton;
    @FXML
    public Button operatorButton;

    @FXML
    public void initialize() {

        User user = getUserFromFile();
        if (user != null) {
            infoLabel.setText("WELCOME TO MEALAPP " + user.getName().toUpperCase() + " " + user.getSurname().toUpperCase() + "!");
        } else {
            infoLabel.setText("WELCOME TO MEALAPP!");
        }
        orderButton.setOnAction(event -> {
            try {
                openAppropriateScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        StyleUtil.styleStartButton(orderButton, "Order",36, "", 14);
        StyleUtil.styleStartButton(operatorButton, "Operator",36, "", 14);
    }

    @FXML
    private void openAppropriateScreen() throws IOException {
        User user = getUserFromFile();

        if (user != null && registrationManager.checkCustomerFileExists(user.getName(), user.getSurname())) {
            registrationManager.appendUserToServerFile(user.getName(), user.getSurname());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Order.fxml"));
            Parent root = loader.load();

            OrderController orderController = loader.getController();
            orderController.setUser(user);

            Stage stage = (Stage) orderButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } else {
            loadFXML("/Registration.fxml");
        }
    }

    @FXML
    private void showAdminLogin(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminLogin.fxml"));
        Parent root = loader.load();

        AdminLoginController controller = loader.getController();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        if (controller.isAuthenticated()) {
            Parent rootAdmin = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AdminScreen.fxml")));
            Stage stageAdmin = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageAdmin.setScene(new Scene(rootAdmin));
        }
    }

    @FXML
    private void openOperatorScreen() throws IOException {
        Parent operatorRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Operator.fxml")));
        Stage stage = (Stage) operatorButton.getScene().getWindow();
        stage.setScene(new Scene(operatorRoot));
    }

    private User getUserFromFile() {
        String directory = Constants.APP_FOLDER_PATH;
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    String[] nameSurname = file.getName().replace(".txt", "").split("_");
                    if (nameSurname.length == 2) {
                        String name = nameSurname[0];
                        String surname = nameSurname[1];
                        if (registrationManager.checkCustomerFileExists(name, surname)) {
                            return new User(name, surname);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void loadFXML(String resourcePath) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourcePath)));
        Stage stage = (Stage) orderButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
