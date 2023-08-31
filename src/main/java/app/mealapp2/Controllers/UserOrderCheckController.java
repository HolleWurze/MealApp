package app.mealapp2.Controllers;

import app.mealapp2.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserOrderCheckController {

        @FXML
        private TextField nameField, surnameField, monthField;
        @FXML
        private Label resultLabel;

    @FXML
    public void checkOrders() {
        String name = nameField.getText();
        String surname = surnameField.getText();
        String month = monthField.getText();

        if (name.isEmpty() || surname.isEmpty() || month.isEmpty()) {
            resultLabel.setText("All fields must be filled!");
            return;
        }

        String year = String.valueOf(LocalDate.now().getYear()); // извлечение текущего года

        Map<String, Integer> cateringOrders = getCateringOrdersForUser(name, surname, year, month);
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : cateringOrders.entrySet()) {
            result.append(String.format("%s ordered %d times in %s catering.\n", name + " " + surname, entry.getValue(), entry.getKey()));
        }

        resultLabel.setText(result.toString());
    }

    private Map<String, Integer> getCateringOrdersForUser(String name, String surname, String year, String month) {
        File folder = new File(Constants.SERVER_FOLDER_PATH + "\\Orders");
        File[] listOfFiles = folder.listFiles();

        Map<String, Integer> orders = new HashMap<>();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.contains(year + "-" + month) && fileName.contains(name) && fileName.contains(surname)) {
                    String[] filenameParts = fileName.split("_");
                    String cateringName = filenameParts[1];
                    orders.put(cateringName, orders.getOrDefault(cateringName, 0) + 1);
                }
            }
        }

        return orders;
    }
}

