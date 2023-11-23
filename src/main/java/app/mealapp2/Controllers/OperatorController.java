package app.mealapp2.Controllers;

import app.mealapp2.Constants;
import app.mealapp2.Entity.Meal;
import app.mealapp2.Entity.Order;
import app.mealapp2.Managers.OperatorManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OperatorController {

    private OperatorManager operatorManager;
    private UserListController userListController;

    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, String> cateringColumn;
    @FXML
    private TableColumn<Order, String> nameColumn;
    @FXML
    private TableColumn<Order, String> mainDishColumn;
    @FXML
    private TableColumn<Order, String> sideDishColumn;
    @FXML
    private TableColumn<Order, String> saladColumn;
    @FXML
    private TableColumn<Order, String> additionColumn;
    @FXML
    private TableColumn<Order, String> waterColumn;
    @FXML
    private TableColumn<Order, String> cibusColumn;
    @FXML
    private Button loadButton;
    @FXML
    private Button createExcelButton;
    @FXML
    private Button goToMainMenuButton;
    @FXML
    private Button checkUserOrderPerMonth;
//    @FXML
//    private Button ordersForGuests;
    @FXML
    private Button checkOrdersButton;
    @FXML
    private Label quantityOrdersToday;

    public OperatorController() {
        operatorManager = new OperatorManager();
    }

    @FXML
    public void initialize() {

        setupTableView();

        loadButton.setOnAction(e -> handleLoadButtonAction());
        createExcelButton.setOnAction(e -> handleCreateExcelButtonAction());
        checkUserOrderPerMonth.setOnAction(event -> {
            try {
                checkUserOrderPerMonth();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void showUserList() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserList.fxml"));
        Parent root = loader.load();
        UserListController controller = loader.getController();

        if (controller.getUsersWhoForgotToOrder().size() != 0) {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("All users ordered today");
            alert.setHeaderText(null);
            alert.setContentText("Great! All users who wanted ordered today.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleLoadButtonAction() {
        Map<String, List<Order>> groupedOrders = operatorManager.loadOrders();
        ordersTable.getItems().clear();
        for (List<Order> orders : groupedOrders.values()) {
            ordersTable.getItems().addAll(orders);
        }
        quantityOrdersToday.setText("Quantity of orders for today: " + ordersTable.getItems().size());
        quantityOrdersToday.setVisible(true);
    }

    @FXML
    public void makeOrdersForGuests() {
        try {
            // Загрузка FXML файла
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GuestsOrders.fxml"));
            Parent root = fxmlLoader.load();

            // Создание новой сцены
            Scene scene = new Scene(root, 1000, 600);

            // Создание нового окна и установка сцены
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Make Orders for Guests");

            // Показать новое окно
            stage.show();
        } catch (Exception e) {
            // Обработка исключений
            e.printStackTrace();
        }
    }


    @FXML
    private void goToMainMenu() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Start.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) goToMainMenuButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void checkUserOrderPerMonth() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserOrderCheck.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    public List<Order> generateOrdersFromFiles() {
        Map<String, List<Order>> ordersGroupedByCatering = operatorManager.loadOrders();
        List<Order> allOrders = new ArrayList<>();

        for (List<Order> orders : ordersGroupedByCatering.values()) {
            allOrders.addAll(orders);
        }

        return allOrders;
    }

    private boolean isHebrew(String text) {
        for (char c : text.toCharArray()) {
            if ((c >= 0x0590 && c <= 0x05FF) || (c >= 0xFB1D && c <= 0xFB4F)) {
                return true;
            }
        }
        return false;
    }

    private CellStyle getHebrewCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle getDefaultCellStyle(XSSFWorkbook workbook) {
        return workbook.createCellStyle(); // Можно настроить стиль по умолчанию, если нужно
    }

    @FXML
    public void handleCreateExcelButtonAction() {
        List<Order> allOrders = generateOrdersFromFiles();

        XSSFWorkbook workbook = new XSSFWorkbook();
        String date = LocalDate.now().toString();
        XSSFSheet sheet = workbook.createSheet("Orders_" + date);

        String[] headers = new String[]{"Name", "Catering", "Main Dish", "Side Dish", "Salads", "Drink", "Comment", "Cibus"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
//        for (Order order : allOrders) {
//            Row row = sheet.createRow(rowNum++);
//            createCell(row, 0, order.getName() + " " + order.getSurname());
//
//            Meal meal = order.getMeal(); // Получаем блюдо напрямую из заказа
//
//            createCell(row, 1, meal.getCatering());
//            createCell(row, 2, meal.getMainDish());
//            createCell(row, 3, meal.getSideDish());
//            createCell(row, 4, meal.getSalads());
//            createCell(row, 6, meal.getWater());
//            createCell(row, 5, meal.getAddition());
//            createCell(row, 7, meal.getCibus() ? "YES" : "NO");
//        }

//        for (Order order : allOrders) {
//            Row row = sheet.createRow(rowNum++);
//            createCell(row, 0, order.getName() + " " + order.getSurname(), workbook);
//            Meal meal = order.getMeal();
//
//            // Создаем ячейки с правильным стилем для каждого значения
//            createCell(row, 1, meal.getCatering(), workbook);
//            createCell(row, 2, meal.getMainDish(), workbook);
//            createCell(row, 3, meal.getSideDish(), workbook);
//            createCell(row, 4, meal.getSalads(), workbook);
//            createCell(row, 5, meal.getWater(), workbook);
//            createCell(row, 6, meal.getAddition(), workbook);
//            createCell(row, 7, meal.getCibus() ? "YES" : "NO", workbook);
//        }

        for (Order order : allOrders) {
            Row row = sheet.createRow(rowNum++);

            // Создаем ячейки для каждого элемента заказа отдельно
            int cellIndex = 0;
            createCell(workbook, row, cellIndex++, order.getName() + " " + order.getSurname());
            Meal meal = order.getMeal();
            createCell(workbook, row, cellIndex++, formatText(meal.getCatering()));
            createCell(workbook, row, cellIndex++, formatText(meal.getMainDish()));
            createCell(workbook, row, cellIndex++, formatText(meal.getSideDish()));
            createCell(workbook, row, cellIndex++, formatText(meal.getSalads()));
            createCell(workbook, row, cellIndex++, formatText(meal.getWater()));
            createCell(workbook, row, cellIndex++, formatText(meal.getAddition()));
            createCell(workbook, row, cellIndex, formatText(meal.getCibus() ? "YES" : "NO"));
        }

        String filePath = Constants.SERVER_FOLDER_PATH + "\\Reports" + "/orders_" + LocalDate.now() + ".xlsx";
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Excel File");
            alert.setContentText("Excel File successfully create!");
            alert.showAndWait();

            openExcelFile(filePath);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Excel File");
            alert.setContentText("Some problems on the server side.. Please try again later");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private String formatText(String text) {
        if (isHebrew(text)) {
            return "\u200F" + text; // Добавляем RLM для текста на иврите
        } else {
            return "\u200E" + text; // Добавляем LRM для текста на английском или русском
        }
    }

//    private void createCell(Row row, int columnCount, String value) {
//        Cell cell = row.createCell(columnCount);
//        cell.setCellValue(value);
//    }

    private void createCell(XSSFWorkbook workbook, Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);

        // Применяем стиль в зависимости от языка
        if (isHebrew(value)) {
            cell.setCellStyle(getHebrewCellStyle(workbook));
        } else {
            cell.setCellStyle(getDefaultCellStyle(workbook));
        }
    }

    private void openExcelFile(String filePath) {
        if (Desktop.isDesktopSupported()) {
            try {
                File file = new File(filePath);
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Opening File");
                alert.setContentText("Error occurred while opening the file.");
                alert.showAndWait();
            }
        }
    }

    private void setupTableView() {
        cateringColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getCatering()));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName() + " " + data.getValue().getSurname()));
        mainDishColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getMainDish()));
        sideDishColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getSideDish()));
        saladColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getSalads()));
        waterColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getWater()));
        additionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getAddition()));
        cibusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getCibus() ? "YES" : "NO"));

        int numberOfColumns = 8;
        cateringColumn.prefWidthProperty().bind(ordersTable.widthProperty().divide(numberOfColumns));
        nameColumn.prefWidthProperty().bind(ordersTable.widthProperty().divide(numberOfColumns));
        mainDishColumn.prefWidthProperty().bind(ordersTable.widthProperty().divide(numberOfColumns));
        sideDishColumn.prefWidthProperty().bind(ordersTable.widthProperty().divide(numberOfColumns));
        saladColumn.prefWidthProperty().bind(ordersTable.widthProperty().divide(numberOfColumns));
        waterColumn.prefWidthProperty().bind(ordersTable.widthProperty().divide(numberOfColumns));
        additionColumn.prefWidthProperty().bind(ordersTable.widthProperty().divide(numberOfColumns));
        cibusColumn.prefWidthProperty().bind(ordersTable.widthProperty().divide(numberOfColumns));
    }
}
