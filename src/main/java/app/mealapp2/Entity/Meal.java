package app.mealapp2.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Meal {
    private String catering;
    private String mainDish;
    private String sideDish;
    private String salads;
    private String addition;
    private String water;
    private boolean cibus;


    // конструктор
    public Meal(String catering, String mainDish, String sideDish, String salads, String addition, String water, boolean cibus) {
        this.catering = catering;
        this.mainDish = mainDish;
        this.sideDish = sideDish;
        this.salads = salads;
        this.addition = addition;
        this.water = water;
        this.cibus = cibus;
    }

    public String toCsvString() {
        return catering + "," + mainDish + "," + sideDish + "," + salads + "," + addition + "," + water + ", " + (cibus ? "YES" : "NO");
    }

    @Override
    public String toString() {
        return catering + ", " + mainDish + ", " + sideDish + ", " + salads + ", " + addition + ", " + water + ", " + (cibus ? "YES" : "NO");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Meal meal = (Meal) obj;
        return Objects.equals(catering, meal.catering) &&
                Objects.equals(mainDish, meal.mainDish) &&
                Objects.equals(sideDish, meal.sideDish) &&
                Objects.equals(salads, meal.salads) &&
                Objects.equals(addition, meal.addition) &&
                Objects.equals(water, meal.water) &&
                cibus == meal.cibus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(catering, mainDish, sideDish, salads, addition, water, cibus);
    }

}
