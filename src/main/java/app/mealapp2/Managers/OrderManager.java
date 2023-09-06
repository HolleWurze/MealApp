package app.mealapp2.Managers;

import app.mealapp2.Constants;
import app.mealapp2.Entity.Meal;
import app.mealapp2.Entity.Order;
import javafx.scene.control.Alert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderManager {

    public String saveOrderToFile(Order order) {
        try {
            // Проверяем доступность серверной папки
            Path serverPath = Paths.get(Constants.SERVER_FOLDER_PATH);
            if (Files.notExists(serverPath)) {
                Files.createDirectories(serverPath);
            }

            // Проверяем наличие блюда в заказе
            Meal meal = order.getMeal();
            if (meal == null) {
                return "Order cannot be saved because it doesn't contain a meal.";
            }
            String cateringName = meal.getCatering().replaceAll("[^a-zA-Z0-9\\._]+", "_"); //ДОБАВЛЕН GET
            String filename = LocalDate.now() + "_" + cateringName + "_" + order.getName() + "_" + order.getSurname() + ".txt";

            // Проверяем, существует ли уже заказ для этого пользователя на сегодняшнюю дату
            String orderFileNamePattern = LocalDate.now() + "_.*_" + order.getName() + "_" + order.getSurname() + "\\.txt";
            Path orderFolderPath = Paths.get(Constants.SERVER_FOLDER_PATH + "\\Orders");
            File folder = orderFolderPath.toFile();
            File[] matchingFiles = folder.listFiles((dir, name) -> name.matches(orderFileNamePattern));

            if (matchingFiles != null && matchingFiles.length > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Save order");
                alert.setContentText("You already have an order for today. If you made a mistake in your order, you can change it by pressing the 'change order' button.");
                alert.showAndWait();
                return "Order for today already exists.";
            }

            // Создаем файл в папке на сервере
            File orderFile = new File(Constants.SERVER_FOLDER_PATH + "\\Orders" + File.separator + filename);
            if (orderFile.createNewFile()) {
                try (FileWriter writer = new FileWriter(orderFile)) {
                    writer.write(order.toCsvString());
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Save order");
                alert.setContentText("Order successfully saved on the server!");
                alert.showAndWait();
                return "Order successfully saved on the server!";
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Save order");
                alert.setContentText("Order has already been created.");
                alert.showAndWait();
                return "Order has already been created.";
            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Save order");
            alert.setContentText("Failed to save order to server. Please try again later.");
            alert.showAndWait();
            return "Failed to save order to server. Please try again later.";
        }
    }

    public static boolean deleteSelectedFavoriteOrder(Order orderToDelete) throws IOException {
        File file = new File(Constants.APP_FOLDER_PATH + "\\" + orderToDelete.getName() + "_" + orderToDelete.getSurname() + "_favorites.txt");

        // Загрузить заказы из файла
        List<Meal> meals = loadFavoritesFromFile(file);
        if (meals == null) {
            return false; // Нечего удалять
        }
        // Удалить выбранный заказ из списка
        boolean isRemoved = meals.remove(orderToDelete.getMeal());
        // Если заказ был успешно удален, перезапишите файл
        if (isRemoved) {
            // Сначала очистите файл
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();

            // Теперь перезапишите файл с оставшимися заказами
            saveFavoritesToFile(meals, file.getAbsolutePath());
        }
        return isRemoved;
    }

    public static String saveFavoritesToFile(List<Meal> favorites, String filePath) {
        // Создание потока вывода в файл в режиме добавления
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.print("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filePath, true))) {
            // Обход по всем элементам списка
            for (Meal meal : favorites) {
                // Запись в файл
                writer.println(meal.toString());
            }
            writer.flush();
            writer.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Save favorite file");
            alert.setContentText("Favorite successfully saved!");
            alert.showAndWait();
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File Not Found");
            alert.setContentText("Favorites does not exist.");
            alert.showAndWait();
            e.printStackTrace();
        }
        return "Favorite file successfully saved on the server!";
    }

    public static List<Meal> loadFavoritesFromFile(File file) throws IOException {
        List<Meal> favoriteOrders = new ArrayList<>();

        if (!file.exists()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("File Not Found");
            alert.setContentText("Favorites file does not exist.");
            alert.showAndWait();
            return favoriteOrders; // return empty list
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));

        // Извлечение имени и фамилии пользователя из имени файла
        String filename = file.getName();
        String userFullName = filename.substring(0, filename.lastIndexOf("_favorites.txt"));
        String[] nameSurname = userFullName.split("_");
        String name = nameSurname[0];
        String surname = nameSurname[1];

        String line;
        while ((line = reader.readLine()) != null) {
            String[] mealData = line.split(",");

            String catering = mealData.length > 0 ? mealData[0].trim() : "";
            String mainDish = mealData.length > 1 ? mealData[1].trim() : "";
            String sideDish = mealData.length > 2 ? mealData[2].trim() : "";
            String salads = mealData.length > 3 ? mealData[3].trim() : "";
            String addition = mealData.length > 4 ? mealData[4].trim() : "";
            String water = mealData.length > 5 ? mealData[5].trim() : "";
            boolean cibus = mealData.length > 6 && "YES".equalsIgnoreCase(mealData[6].trim());

            Meal meal = new Meal(catering, mainDish, sideDish, salads, addition, water, cibus);
            favoriteOrders.add(meal);
        }

        reader.close();
        return favoriteOrders;
    }

    public Order getOrderFromFile(Path filePath) {
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (lines.size() < 2) {  // если в файле меньше 2 строк, то что-то пошло не так
            return null;
        }

        // Извлечь имя и фамилию
        String[] nameAndSurname = lines.get(0).split(",");
        if (nameAndSurname.length != 2) {
            return null;  // неправильный формат файла
        }
        String name = nameAndSurname[0];
        String surname = nameAndSurname[1];

        // Извлечь значения заказа
        String[] orderFields = lines.get(1).split(",");
        if (orderFields.length < 7) {
            return null;  // неправильный формат файла
        }

        String catering = orderFields[0];
        String mainDish = orderFields[1];
        String sideDish = orderFields[2];
        String salads = orderFields[3];
        String addition = orderFields[4];
        String water = orderFields[5];
        boolean cibus = "YES".equals(orderFields[6]);

        Meal meal = new Meal(catering, mainDish, sideDish, salads, addition, water, cibus);
        return new Order(name, surname, meal);
    }
}