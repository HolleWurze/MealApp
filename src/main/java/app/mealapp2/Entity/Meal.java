package app.mealapp2.Entity;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.*;

import java.util.Objects;

@Data
public class Meal {

    private SimpleStringProperty catering;
    private SimpleStringProperty mainDish;
    private SimpleStringProperty sideDish;
    private SimpleStringProperty salads;
    private SimpleStringProperty water;
    private SimpleStringProperty addition;
    private SimpleBooleanProperty cibus;


    public Meal(String catering, String mainDish, String sideDish, String salads, String water, String addition, boolean cibus) {

        this.catering = new SimpleStringProperty(catering);
        this.mainDish = new SimpleStringProperty(mainDish);
        this.sideDish = new SimpleStringProperty(sideDish);
        this.salads = new SimpleStringProperty(salads);
        this.water = new SimpleStringProperty(water);
        this.addition = new SimpleStringProperty(addition);
        this.cibus = new SimpleBooleanProperty(cibus);
    }

    public String toCsvString() {
        return catering.get() + "#" +
                mainDish.get() + "#" +
                sideDish.get() + "#" +
                salads.get() + "#" +
                water.get() + "#" +
                addition.get() + "#" +
                (cibus.get() ? "YES" : "NO");
    }

    @Override
    public String toString() {
        return catering.get() + "#" +
                mainDish.get() + "#" +
                sideDish.get() + "#" +
                salads.get() + "#" +
                water.get() + "#" +
                addition.get() + "#" +
                (cibus.get() ? "YES" : "NO");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Meal meal = (Meal) obj;
        return Objects.equals(catering.get(), meal.catering.get()) &&
                Objects.equals(mainDish.get(), meal.mainDish.get()) &&
                Objects.equals(sideDish.get(), meal.sideDish.get()) &&
                Objects.equals(salads.get(), meal.salads.get()) &&
                Objects.equals(water.get(), meal.water.get()) &&
                Objects.equals(addition.get(), meal.addition.get()) &&
                cibus.get() == meal.cibus.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(catering.get(), mainDish.get(), sideDish.get(), salads.get(), water.get(), addition.get(), cibus.get());
    }

    public String getCatering() {
        return catering.get();
    }
    public void setCatering(String value) {
        catering.set(value);
    }
    public String getMainDish() {
        return mainDish.get();
    }
    public void setMainDish(String value) {
        mainDish.set(value);
    }
    public String getSideDish() {
        return sideDish.get();
    }
    public void setSideDish(String value) {
        sideDish.set(value);
    }
    public String getSalads() {
        return salads.get();
    }
    public void setSalads(String value) {
        salads.set(value);
    }
    public String getAddition() {
        return addition.get();
    }
    public void setAddition(String value) {
        addition.set(value);
    }
    public String getWater() {
        return water.get();
    }
    public void setWater(String value) {
        water.set(value);
    }
    public boolean getCibus() {
        return cibus.get();
    }
    public void setCibus(boolean value) {
        cibus.set(value);
    }
}
