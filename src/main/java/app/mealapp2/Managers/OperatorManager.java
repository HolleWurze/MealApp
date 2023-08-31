package app.mealapp2.Managers;

import app.mealapp2.Constants;
import app.mealapp2.Entity.Meal;
import app.mealapp2.Entity.Order;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class OperatorManager {
    public Map<String, List<Order>> loadOrders() {
        File folder = new File(Constants.SERVER_FOLDER_PATH + "\\Orders");
        File[] listOfFiles = folder.listFiles();

        Map<String, List<Order>> ordersGroupedByCatering = new TreeMap<>();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().startsWith(LocalDate.now().toString())) {
                String[] filenameParts = file.getName().split("_");
                String cateringName = filenameParts[1];
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String nameSurnameLine = reader.readLine();
                    String[] nameSurname = nameSurnameLine.split(",");

                    String line = reader.readLine();
                    if (line != null) {
                        String[] mealData = line.split(",");
                        if (mealData.length < 7) {
                            continue;
                        }
                        boolean cibusValue = "YES".equalsIgnoreCase(mealData[6].trim());
                        System.out.println(cibusValue); //false
                        Meal meal = new Meal(mealData[0], mealData[1], mealData[2], mealData[3], mealData[4], mealData[5], cibusValue);
                        ordersGroupedByCatering.computeIfAbsent(cateringName, k -> new ArrayList<>()).add(new Order(nameSurname[0], nameSurname[1], meal));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (List<Order> orders : ordersGroupedByCatering.values()) {
            orders.sort(Comparator.comparing(Order::getSurname));
        }
        return ordersGroupedByCatering;
    }
}

