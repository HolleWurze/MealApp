package app.mealapp2.Controllers;

import app.mealapp2.Constants;
import app.mealapp2.Entity.Meal;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GuestsOrdersController implements Initializable {
    //right side
    @FXML
    private TableView<Meal> mealTable;
    @FXML
    private TableColumn<Meal, String> cateringColumn;
    @FXML
    private TableColumn<Meal, String> mainDishColumn;
    @FXML
    private TableColumn<Meal, String> sideDishColumn;
    @FXML
    private TableColumn<Meal, String> saladColumn;
    @FXML
    private TableColumn<Meal, String> waterColumn;
    @FXML
    private TableColumn<Meal, String> commentColumn;
    @FXML
    private TableColumn<Meal, Boolean> cibusColumn;
    //left side
    @FXML
    private TextField catering;
    @FXML
    private TextField mainDish;
    @FXML
    private TextField sideDish;
    @FXML
    private TextField salads;
    @FXML
    private TextField water;
    @FXML
    private TextField comment;
    @FXML
    private TextField quantity;
    @FXML
    private CheckBox cibusCheckBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cateringColumn.setCellValueFactory(new PropertyValueFactory<Meal, String>("catering"));
        cateringColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cateringColumn.setOnEditCommit(event -> {
            Meal meal = event.getRowValue();
            meal.setCatering(String.valueOf(event.getNewValue()));
        });
        mainDishColumn.setCellValueFactory(new PropertyValueFactory<Meal, String>("mainDish"));
        mainDishColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        mainDishColumn.setOnEditCommit(event -> {
            Meal meal = event.getRowValue();
            meal.setMainDish(String.valueOf(event.getNewValue()));
        });
        sideDishColumn.setCellValueFactory(new PropertyValueFactory<Meal, String>("sideDish"));
        sideDishColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        sideDishColumn.setOnEditCommit(event -> {
            Meal meal = event.getRowValue();
            meal.setSideDish(String.valueOf(event.getNewValue()));
        });
        saladColumn.setCellValueFactory(new PropertyValueFactory<Meal, String>("salads"));
        saladColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        saladColumn.setOnEditCommit(event -> {
            Meal meal = event.getRowValue();
            meal.setSalads(String.valueOf(event.getNewValue()));
        });
        waterColumn.setCellValueFactory(new PropertyValueFactory<Meal, String>("water"));
        waterColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        waterColumn.setOnEditCommit(event -> {
            Meal meal = event.getRowValue();
            meal.setWater(String.valueOf(event.getNewValue()));
        });
        commentColumn.setCellValueFactory(new PropertyValueFactory<Meal, String>("addition"));
        commentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        commentColumn.setOnEditCommit(event -> {
            Meal meal = event.getRowValue();
            meal.setAddition(String.valueOf(event.getNewValue()));
        });
        cibusColumn.setCellValueFactory(new PropertyValueFactory<Meal, Boolean>("cibus"));
        cibusColumn.setCellFactory(TextFieldTableCell.forTableColumn(new YesNoStringConverter()));
        cibusColumn.setOnEditCommit(event -> {
            Meal meal = event.getRowValue();
            meal.setCibus(event.getNewValue());
        });
    }

    @FXML
    public void saveOrdersToFile() {
        deleteOldOrderFiles();
        try {
            int quantityOfOrders = Integer.parseInt(quantity.getText());
            int totalOrdersInTable = mealTable.getItems().size();
            int totalOrders = quantityOfOrders + totalOrdersInTable;

            String filePath = Constants.SERVER_FOLDER_PATH + "\\Orders\\" + LocalDate.now() + "_Guests_order_" + totalOrders + ".txt";

            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {

                String cateringValue = catering.getText().trim();
                String mainDishValue = mainDish.getText().trim();
                String sideDishValue = sideDish.getText().trim();
                String saladsValue = salads.getText().trim();
                String waterValue = water.getText().trim();
                String commentValue = comment.getText().trim();

                Meal mealFromLeft = new Meal(cateringValue, mainDishValue, sideDishValue, saladsValue, waterValue, commentValue, cibusCheckBox.isSelected());

                String duplicateOrderString = "D|" + mealFromLeft;

                for (int i = 0; i < quantityOfOrders; i++) {
                    writer.write(duplicateOrderString);
                    writer.newLine();
                }

                for (Meal rightMeal : mealTable.getItems()) {
                    String individualOrderString = "I|" + rightMeal.toString();
                    writer.write(individualOrderString);
                    writer.newLine();
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Successfully saved guest orders!");
                alert.showAndWait();

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error while saving to file: " + e.getMessage());
                alert.showAndWait();
            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error in quantity: " + e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    private void addRow() {
        Meal newMeal = new Meal("", "", "", "", "", "", false);
        mealTable.getItems().add(newMeal);
    }

    @FXML
    private void deleteSelectedRow() {
        Meal selectedMeal = mealTable.getSelectionModel().getSelectedItem();
        if (selectedMeal != null) {
            mealTable.getItems().remove(selectedMeal);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("No row selected");
            alert.showAndWait();
        }
    }

    @FXML
    public void changeGuestsOrders() {
        String filePath = null;
        try {
            filePath = getCurrentGuestOrderFileName();
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Guest order file not found!");
            alert.showAndWait();
            return;
        }

        List<Meal> individualOrders = new ArrayList<>();
        Meal duplicateOrder = null;
        int duplicateOrderCount = 0;
        int individualOrderCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.substring(2).split(",");

                if (line.startsWith("D|")) {
                    duplicateOrderCount++;
                    if (duplicateOrder == null) {
                        duplicateOrder = new Meal(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim(), customParseToBoolean(parts[6].trim()));
                    }
                } else if (line.startsWith("I|")) {
                    individualOrderCount++;
                    Meal individualMeal = new Meal(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim(), customParseToBoolean(parts[6].trim()));
                    individualOrders.add(individualMeal);
                }
            }

            int totalOrders = duplicateOrderCount + individualOrderCount;

            if (duplicateOrder != null) {
                catering.setText(duplicateOrder.getCatering());
                mainDish.setText(duplicateOrder.getMainDish());
                sideDish.setText(duplicateOrder.getSideDish());
                salads.setText(duplicateOrder.getSalads());
                water.setText(duplicateOrder.getWater());
                comment.setText(duplicateOrder.getAddition());
                quantity.setText(String.valueOf(duplicateOrderCount));
                cibusCheckBox.setSelected(duplicateOrder.getCibus());
            }

            mealTable.getItems().clear();
            mealTable.getItems().addAll(individualOrders);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error while reading the file: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteGuestsOrdersFile() {
        String filePath = Constants.SERVER_FOLDER_PATH + "\\Orders\\" + LocalDate.now() + "_Guests_order.txt";
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("File was deleted successfully");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to delete the file");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Guest order file not found!");
            alert.showAndWait();
        }
    }

    public class YesNoStringConverter extends StringConverter<Boolean> {

        @Override
        public String toString(Boolean object) {
            if (object == null) {
                return "NO";
            }
            return object ? "YES" : "NO";
        }

        @Override
        public Boolean fromString(String string) {
            return "YES".trim().equalsIgnoreCase(string);
        }
    }

    private String getCurrentGuestOrderFileName() throws FileNotFoundException{
        String baseFilePath = Constants.SERVER_FOLDER_PATH + "\\Orders\\" + LocalDate.now() + "_Guests_order_";
        int orderCount = 0;
        int maxAttempts = 100;

        while (orderCount < maxAttempts) {
            File file = new File(baseFilePath + orderCount + ".txt");
            if (file.exists()) {
                return baseFilePath + orderCount + ".txt";
            }
            orderCount++;
        }
        throw new FileNotFoundException("Guest order file not found!");
    }

    private static boolean customParseToBoolean(String value) {
        if ("YES".equalsIgnoreCase(value)) {
            return true;
        } else if ("NO".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid value for YES/NO conversion: " + value);
        }
    }

    public void deleteOldOrderFiles() {
        File folder = new File(Constants.SERVER_FOLDER_PATH + "\\Orders");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    if (fileName.matches(LocalDate.now() + "_Guests_order_" + "_.*_" + ".txt")) {
                        file.delete();
                    }
                }
            }
        }
    }
}
