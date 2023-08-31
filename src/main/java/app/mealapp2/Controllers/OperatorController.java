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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
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

    @FXML
    public void checkUserOrderPerMonth() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserOrderCheck.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @FXML
    public void handleCreateExcelButtonAction() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String date = LocalDate.now().toString();
        XSSFSheet sheet = workbook.createSheet("Orders_" + date);

        // Создание строки заголовков
        String[] headers = new String[]{"Name", "Catering", "Main Dish", "Side Dish", "Salads", "Drink", "Comment", "Cibus"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;  // начало с 1, так как 0-ая строка используется для заголовков
        for (Order order : ordersTable.getItems()) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, order.getName() + " " + order.getSurname());

            Meal meal = order.getMeal(); // Получаем блюдо напрямую из заказа

            createCell(row, 1, meal.getCatering());
            createCell(row, 2, meal.getMainDish());
            createCell(row, 3, meal.getSideDish());
            createCell(row, 4, meal.getSalads());
            createCell(row, 6, meal.getWater());
            createCell(row, 5, meal.getAddition());
            createCell(row, 7, meal.isCibus() ? "YES" : "NO");
        }
        //Создание файла excel по заданному пути
        try (FileOutputStream outputStream = new FileOutputStream(Constants.SERVER_FOLDER_PATH + "\\Reports" + "/orders_" + LocalDate.now() + ".xlsx")) {
            workbook.write(outputStream);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Excel File");
            alert.setContentText("Excel File successfully create!");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Excel File");
            alert.setContentText("Some problems on the server side.. Please try again later");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void createCell(Row row, int columnCount, String value) {
        Cell cell = row.createCell(columnCount);
        cell.setCellValue(value);
    }

    private void setupTableView() {
        cateringColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getCatering()));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName() + " " + data.getValue().getSurname()));
        mainDishColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getMainDish()));
        sideDishColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getSideDish()));
        saladColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getSalads()));
        waterColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getWater()));
        additionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().getAddition()));
        cibusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMeal().isCibus() ? "YES" : "NO"));

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