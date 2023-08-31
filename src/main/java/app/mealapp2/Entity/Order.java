package app.mealapp2.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
public class Order {

    private String name;
    private String surname;
    private Meal meal;

    public Order(String name, String surname, Meal meal) {
        this.name = name;
        this.surname = surname;
        this.meal = meal;
    }

    public Meal getMeal() {
        return meal;
    }

    public String toCsvString() {
        return name + "," + surname + "\n" + meal.toCsvString();
    }

    @Override
    public String toString() {
        return this.getMeal().getCatering() + ", " + this.getMeal().getMainDish() + ", " +
                this.getMeal().getSideDish() + ", " + this.getMeal().getSalads() + ", " +
                this.getMeal().getAddition() + ", " + this.getMeal().getWater() + ", " +
                this.getMeal().isCibus();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Order order = (Order) obj;
        return Objects.equals(name, order.name) &&
                Objects.equals(surname, order.surname) &&
                Objects.equals(meal, order.meal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, meal);
    }
}

