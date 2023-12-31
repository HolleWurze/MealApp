package app.mealapp2.Controllers;

import app.mealapp2.Constants;
import app.mealapp2.Entity.Meal;
import app.mealapp2.Entity.Order;
import app.mealapp2.Entity.User;
import app.mealapp2.Managers.OrderManager;
import app.mealapp2.Storage.CateringDataStore;
import app.mealapp2.Styles.StyleUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderController {

    private OrderManager orderManager = new OrderManager();
    private User user;
    private List<Meal> favorites = new ArrayList<>();
    List<Order> favoriteOrders = new ArrayList<>();

    @FXML
    private Button goToMainMenuButton;
    @FXML
    private Button deleteOrderButton;
    @FXML
    private Button confirmChangesButton;
    @FXML
    private Button placeOrderButton;
    @FXML
    private Button changeOrderButton;
    @FXML
    private Button addToFavoritesButton;
    @FXML
    private Button showFavoritesButton;
    @FXML
    private Button deleteFavoritesButton;
    @FXML
    public ImageView menuImageView;
    @FXML
    public ChoiceBox<String> cateringChoiceBox;
    @FXML
    private TextField mainDish;
    @FXML
    private TextField sideDish;
    @FXML
    private TextField salads;
    @FXML
    private TextField addition;
    @FXML
    private TextField water;
    @FXML
    private CheckBox cibusCheckBox;
    @FXML
    private Label infoLabel;
    @FXML
    private TableView<Order> favoritesTableView;
    @FXML
    private TableColumn<Order, String> cateringColumn;
    @FXML
    private TableColumn<Order, String> mainDishColumn;
    @FXML
    private TableColumn<Order, String> sideDishColumn;
    @FXML
    private TableColumn<Order, String> saladsColumn;
    @FXML
    private TableColumn<Order, String> additionColumn;
    @FXML
    private TableColumn<Order, String> waterColumn;
    @FXML
    private TableColumn<Order, String> cibusColumn;

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    public void initialize() {
        Tooltip sibusTooltip = new Tooltip("Cibus card");
        Tooltip.install(cibusCheckBox, sibusTooltip);

        favoritesTableView.setVisible(false);
        favorites = new ArrayList<>();
        try {
            fillFavoritesFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> cateringNames = CateringDataStore.getAllCateringNames();
        cateringChoiceBox.getItems().addAll(cateringNames);


        cateringChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String imagePath = CateringDataStore.getImagePathForCatering(newValue);
                Image menuImage = new Image("file:" + imagePath);
                menuImageView.setImage(menuImage);
            } else {
                menuImageView.setImage(null);
            }
        });

        StyleUtil.stylePlaceOrderButton(placeOrderButton, "Place Order");
        StyleUtil.styleAllButton(changeOrderButton, "Change Order", "✎", 20);
        StyleUtil.styleAllButton(deleteOrderButton, "Delete Order", "❌", 20);
        StyleUtil.styleAllButton(addToFavoritesButton, "Add to Favorites", "➕", 20);
        StyleUtil.styleAllButton(showFavoritesButton, "Show Favorites", "\uD83D\uDD0D", 20);
        StyleUtil.styleAllButton(deleteFavoritesButton, "Delete Favorite", "\uD83D\uDDD1", 20);
        StyleUtil.styleAllButton(confirmChangesButton, "Confirm Changes", "✅", 14);
        StyleUtil.styleAllButton(goToMainMenuButton, "Back to Main Menu", "\uD83D\uDD19", 14);
    }

    @FXML
    public void addToFavorites() {
        String cateringValue = (cateringChoiceBox.getValue() == null) ? "" : cateringChoiceBox.getValue();
        String mainDishValue = mainDish.getText().trim();
        String sideDishValue = sideDish.getText().trim();
        String saladsValue = salads.getText().trim();
        String additionValue = addition.getText().trim();
        String waterValue = water.getText().trim();
        boolean cibusValue = cibusCheckBox.isSelected();

        boolean noCatering = cateringValue.isEmpty();
        boolean noDish = mainDishValue.isEmpty() && sideDishValue.isEmpty() && saladsValue.isEmpty() && additionValue.isEmpty() && waterValue.isEmpty(); //&& cibusValue

        if (noCatering && noDish) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Incomplete Order");
            alert.setContentText("Both catering and at least one dish must be selected.");
            alert.showAndWait();
            return;
        }

        if (noCatering) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Catering Not Chosen");
            alert.setContentText("Catering must be selected.");
            alert.showAndWait();
            return;
        }

        if (noDish) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No Dishes Selected");
            alert.setContentText("At least one dish must be selected along with the catering.");
            alert.showAndWait();
            return;
        }

        Meal meal = new Meal(cateringValue, mainDishValue, sideDishValue, saladsValue, waterValue, additionValue, cibusValue); //cibusCheckBox.isSelected()

        if (favorites.contains(meal)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Duplicate Favorite");
            alert.setContentText("This meal is already in your favorites.");
            alert.showAndWait();
            return;
        }

        String filePath = Constants.APP_FOLDER_PATH + "\\" + user.getName() + "_" + user.getSurname() + "_favorites.txt";

        favorites.add(meal);
        OrderManager.saveFavoritesToFile(favorites, filePath);
    }

    @FXML
    public void showFavorites() throws IOException {
        loadFavorites();

        favoritesTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Order selectedOrder = favoritesTableView.getSelectionModel().getSelectedItem();
                if (selectedOrder != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to order this favorite order?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        Meal meal = selectedOrder.getMeal();
                        cateringChoiceBox.setValue(meal.getCatering());
                        mainDish.setText(meal.getMainDish());
                        sideDish.setText(meal.getSideDish());
                        salads.setText(meal.getSalads());
                        addition.setText(meal.getAddition());
                        water.setText(meal.getWater());
                        cibusCheckBox.setSelected(meal.getCibus());

                        Alert alertConfirm = new Alert(Alert.AlertType.INFORMATION);
                        alertConfirm.setTitle("Success");
                        alertConfirm.setHeaderText("Your favorite has been upload");
                        alertConfirm.setContentText("Now all you have to do is - click the \"Place Order\" button!");
                        alertConfirm.showAndWait();
                        favoritesTableView.setVisible(false);
                    }
                }
            }
        });
    }

    @FXML
    public void deleteFavorites() throws IOException {
        loadFavorites();
        favoriteOrders.clear();

        favoritesTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Order selectedOrder = favoritesTableView.getSelectionModel().getSelectedItem();
                if (selectedOrder != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this favorite order?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        try {
                            Meal selectedMeal = selectedOrder.getMeal();
                            boolean isRemoved = favorites.remove(selectedMeal);
                            if (isRemoved) {
                                OrderManager.deleteSelectedFavoriteOrder(selectedOrder);
                            }
                            if (isRemoved) {
                                favorites.remove(selectedOrder);
                                favoriteOrders = favorites.stream()
                                        .map(meal -> new Order(user.getName(), user.getSurname(), meal))
                                        .collect(Collectors.toList());
                                favoritesTableView.setItems(FXCollections.observableArrayList(favoriteOrders));
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        favoritesTableView.setVisible(false);
                    }
                }
            }
        });
    }

    private void loadFavorites() {

        if (favorites.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText("No Favorite");
            alert.setContentText("You don't have any favorites orders yet, if you want to add your first order, enter it and then click the \"Add to Favorites\" button");
            alert.showAndWait();
            return;
        }

        List<Order> favoriteOrders = favorites.stream()
                .map(meal -> new Order(user.getName(), user.getSurname(), meal))
                .collect(Collectors.toList());
        favoritesTableView.setItems(FXCollections.observableArrayList(favoriteOrders));

        favoritesTableView.setVisible(true);

        setupTableView();
    }

    private void fillFavoritesFromFile() throws IOException {
        favorites.clear();

        File appFolderPath = new File(Constants.APP_FOLDER_PATH);
        File[] files = appFolderPath.listFiles((dir, name) -> name.endsWith("_favorites.txt"));
        if (files != null) {
            for (File file : files) {
                List<Meal> meals = OrderManager.loadFavoritesFromFile(file);
                if (!meals.isEmpty()) {
                    favorites.addAll(meals);
                }
            }
        }
    }

    private boolean isHebrew(String text) {
        for (char c : text.toCharArray()) {
            if ((c >= 0x0590 && c <= 0x05FF) || (c >= 0xFB1D && c <= 0xFB4F)) {
                return true;
            }
        }
        return false;
    }

    private String formatText(String text) {
        if (isHebrew(text)) {
            return "\u200F" + text; // Добавляем символ RTL
        } else {
            return text;
        }
    }

    @FXML
    public void placeOrder() {
        String cateringValue = cateringChoiceBox.getValue();
        String mainDishValue = mainDish.getText().trim();
        String sideDishValue = sideDish.getText().trim();
        String saladsValue = salads.getText().trim();
        String additionValue = addition.getText().trim();
        String waterValue = water.getText().trim();

        //boolean isCateringValueEmpty = (cateringValue == null || cateringValue.isEmpty());

        if (cateringChoiceBox.getValue() == null || cateringChoiceBox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Empty Catering!");
            alert.setContentText("You forgot to choose catering!");
            alert.showAndWait();
            return;
        }

        if (mainDishValue.isEmpty() && sideDishValue.isEmpty()
                && saladsValue.isEmpty() && additionValue.isEmpty() && waterValue.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Empty Input");
            alert.setContentText("At least one field must be filled.");
            alert.showAndWait();
            return;
        }

        Meal cateringMeal = new Meal(
                formatText(cateringValue),
                formatText(mainDishValue),
                formatText(sideDishValue),
                formatText(saladsValue),
                formatText(waterValue),
                formatText(additionValue),
                cibusCheckBox.isSelected()
        );

        Order order = new Order(user.getName(), user.getSurname(), cateringMeal);

        try {
            String message = orderManager.saveOrderToFile(order);
            infoLabel.setText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void changeOrder() {
        setOtherButtonsDisabled(true);
        LocalDate today = LocalDate.now();

        String orderFileNamePattern = today + "_.*_" + user.getName() + "_" + user.getSurname() + "\\.txt";
        Path orderFolderPath = Paths.get(Constants.SERVER_FOLDER_PATH, "Orders");
        File folder = orderFolderPath.toFile();
        File[] matchingFiles = folder.listFiles((dir, name) -> name.matches(orderFileNamePattern));

        if (matchingFiles != null && matchingFiles.length > 0) {
            Path orderFilePath = matchingFiles[0].toPath();

            String[] parts = orderFilePath.getFileName().toString().split("_");
            String cateringNameFromFile = parts.length > 2 ? parts[1] : "";

            Order orderFromFile = orderManager.getOrderFromFile(orderFilePath);

            if (orderFromFile != null) {

                Meal mealFromFile = orderFromFile.getMeal();

                cateringChoiceBox.setValue(formatText(mealFromFile.getCatering()));
                mainDish.setText(formatText(mealFromFile.getMainDish()));
                sideDish.setText(formatText(mealFromFile.getSideDish()));
                salads.setText(formatText(mealFromFile.getSalads()));
                addition.setText(formatText(mealFromFile.getAddition()));
                water.setText(formatText(mealFromFile.getWater()));
                cibusCheckBox.setSelected(mealFromFile.getCibus());

                confirmChangesButton.setVisible(true);
            } else {
                showAlert("Change Order", "Error reading order from file.");
                setOtherButtonsDisabled(false);
            }
        } else {
            showAlert("Change Order", "You haven't made an order for today.");
            setOtherButtonsDisabled(false);
        }
    }

    @FXML
    private void deleteOrder() {
        LocalDate today = LocalDate.now();
        String orderFileNamePattern = today + "_.*_" + user.getName() + "_" + user.getSurname() + "\\.txt";
        Path orderFolderPath = Paths.get(Constants.SERVER_FOLDER_PATH, "Orders");
        File folder = orderFolderPath.toFile();
        File[] matchingFiles = folder.listFiles((dir, name) -> name.matches(orderFileNamePattern));

        if (matchingFiles != null && matchingFiles.length > 0) {
            try {
                Files.delete(matchingFiles[0].toPath());

                mainDish.setText("");
                sideDish.setText("");
                salads.setText("");
                addition.setText("");
                water.setText("");
                cibusCheckBox.setSelected(false);
                showAlert("Success", "Order deleted successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while deleting your order.");
            }
        } else {
            showAlert("Info", "No order found for today to delete.");
        }
    }

    private boolean areOrderFieldsEmpty() {
        return mainDish.getText().trim().isEmpty() &&
                sideDish.getText().trim().isEmpty() &&
                salads.getText().trim().isEmpty() &&
                addition.getText().trim().isEmpty() &&
                water.getText().trim().isEmpty();
    }

    @FXML
    public void confirmChanges() {
        if (areOrderFieldsEmpty()) {
            Alert confirmDeletionAlert = new Alert(Alert.AlertType.CONFIRMATION, "It seems you want to delete the order.\nDo you really want to do this?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirmDeletionAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                deleteOrder();
                confirmChangesButton.setVisible(false);
            } else {
                return;
            }
        } else {

            String cateringName = cateringChoiceBox.getValue().replaceAll("[^a-zA-Z0-9\\._]+", "_");
            String orderFileName = LocalDate.now() + "_" + cateringName + "_" + user.getName() + "_" + user.getSurname() + ".txt";

            Path orderFilePath = Paths.get(Constants.SERVER_FOLDER_PATH, "Orders", orderFileName);

            try (BufferedWriter writer = Files.newBufferedWriter(orderFilePath)) {
                writer.write(user.getName() + "," + user.getSurname());
                writer.newLine();

                String formattedCatering = formatText(cateringChoiceBox.getValue());
                String formattedMainDish = formatText(mainDish.getText());
                String formattedSideDish = formatText(sideDish.getText());
                String formattedSalads = formatText(salads.getText());
                String formattedAddition = formatText(addition.getText());
                String formattedWater = formatText(water.getText());
                String formattedCibus = cibusCheckBox.isSelected() ? "YES" : "NO";

                writer.write(String.join("#", formattedCatering, formattedMainDish, formattedSideDish, formattedSalads, formattedWater, formattedAddition, formattedCibus));

                confirmChangesButton.setVisible(false);
                showAlert("Success", "Your order has been successfully updated");

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while updating your order");
            }
        }
        setOtherButtonsDisabled(false);
        deleteOrderButton.setVisible(true);
    }

    private void setOtherButtonsDisabled(boolean disabled) {
        deleteOrderButton.setDisable(disabled);
        placeOrderButton.setDisable(disabled);
        changeOrderButton.setDisable(disabled);
        addToFavoritesButton.setDisable(disabled);
        showFavoritesButton.setDisable(disabled);
        deleteFavoritesButton.setDisable(disabled);
    }

    private void showAlert(String title, String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }

    @FXML
    private void goToMainMenu() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Start.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) goToMainMenuButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void setupTableView() {
        // Установка cellValueFactory для каждого столбца
        cateringColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMeal().getCatering()));
        mainDishColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMeal().getMainDish()));
        sideDishColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMeal().getSideDish()));
        saladsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMeal().getSalads()));
        additionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMeal().getAddition()));
        waterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMeal().getWater()));
        cibusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMeal().getCibus() ? "YES" : "NO"));

        int numberOfColumns = 7;
        cateringColumn.prefWidthProperty().bind(favoritesTableView.widthProperty().divide(numberOfColumns));
        mainDishColumn.prefWidthProperty().bind(favoritesTableView.widthProperty().divide(numberOfColumns));
        sideDishColumn.prefWidthProperty().bind(favoritesTableView.widthProperty().divide(numberOfColumns));
        saladsColumn.prefWidthProperty().bind(favoritesTableView.widthProperty().divide(numberOfColumns));
        additionColumn.prefWidthProperty().bind(favoritesTableView.widthProperty().divide(numberOfColumns));
        waterColumn.prefWidthProperty().bind(favoritesTableView.widthProperty().divide(numberOfColumns));
        cibusColumn.prefWidthProperty().bind(favoritesTableView.widthProperty().divide(numberOfColumns));
    }
}