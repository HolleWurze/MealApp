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

        Comparator<String> cateringComparator = (k1, k2) -> {
            if ("Guest".equals(k1)) return -1;
            if ("Guest".equals(k2)) return 1;
            return k1.compareTo(k2);
        };

        Map<String, List<Order>> ordersGroupedByCatering = new TreeMap<>(cateringComparator);

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().startsWith(LocalDate.now().toString())) {
                if (file.getName().contains("Guests_order")) {
                    processGuestOrderFile(file, ordersGroupedByCatering);
                } else {
                    processIndividualOrderFile(file, ordersGroupedByCatering);
                }
            }
        }

        for (List<Order> orders : ordersGroupedByCatering.values()) {
            orders.sort(Comparator.comparing(Order::getSurname));
        }
        return ordersGroupedByCatering;
    }

    private void processIndividualOrderFile(File file, Map<String, List<Order>> ordersMap) {
        String[] filenameParts = file.getName().split("_");
        String cateringName = filenameParts[1];
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String nameSurnameLine = reader.readLine();
            String[] nameSurname = nameSurnameLine.split(",");

            String line = reader.readLine();
            if (line != null) {
                String[] mealData = line.split("#");
                if (mealData.length < 7) {
                    return;
                }
                boolean cibusValue = "YES".equalsIgnoreCase(mealData[6].trim());
                Meal meal = new Meal(mealData[0], mealData[1], mealData[2], mealData[3], mealData[4], mealData[5], cibusValue);
                ordersMap.computeIfAbsent(cateringName, k -> new ArrayList<>()).add(new Order(nameSurname[0], nameSurname[1], meal));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processGuestOrderFile(File file, Map<String, List<Order>> ordersMap) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitData = line.split("\\|", 2);
                if (splitData.length < 2) {
                    continue;
                }

                String[] mealData = splitData[1].split(",");
                if (mealData.length < 7) {
                    continue;
                }

                boolean cibusValue = "YES".equalsIgnoreCase(mealData[6].trim());
                Meal meal = new Meal(mealData[0], mealData[1], mealData[2], mealData[3], mealData[4], mealData[5], cibusValue);
                ordersMap.computeIfAbsent("Guest", k -> new ArrayList<>()).add(new Order("Guest", "", meal));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


