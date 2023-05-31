package csf.iss.server.models;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Nutrition {
    private String name;
    private double calories;
    private double servingSize;
    private double fatTotal;
    private double fatSaturated;
    private double protein;
    private double sodium;
    private double cholesterol;
    private double carbohydrates;
    private double fibre;
    private double sugar;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getCalories() {
        return calories;
    }
    public void setCalories(double calories) {
        this.calories = calories;
    }
    public double getServingSize() {
        return servingSize;
    }
    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }
    public double getFatTotal() {
        return fatTotal;
    }
    public void setFatTotal(double fatTotal) {
        this.fatTotal = fatTotal;
    }
    public double getFatSaturated() {
        return fatSaturated;
    }
    public void setFatSaturated(double fatSaturated) {
        this.fatSaturated = fatSaturated;
    }
    public double getProtein() {
        return protein;
    }
    public void setProtein(double protein) {
        this.protein = protein;
    }
    public double getSodium() {
        return sodium;
    }
    public void setSodium(double sodium) {
        this.sodium = sodium;
    }
    public double getCholesterol() {
        return cholesterol;
    }
    public void setCholesterol(double cholesterol) {
        this.cholesterol = cholesterol;
    }
    public double getCarbohydrates() {
        return carbohydrates;
    }
    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }
    public double getFibre() {
        return fibre;
    }
    public void setFibre(double fibre) {
        this.fibre = fibre;
    }
    public double getSugar() {
        return sugar;
    }
    public void setSugar(double sugar) {
        this.sugar = sugar;
    }
    public Nutrition() {
    }

    public Nutrition(String name, double calories, double servingSize, double fatTotal, double fatSaturated, double protein,
        double sodium, double cholesterol, double carbohydrates, double fibre, double sugar) {
        this.name = name;
        this.calories = calories;
        this.servingSize = servingSize;
        this.fatTotal = fatTotal;
        this.fatSaturated = fatSaturated;
        this.protein = protein;
        this.sodium = sodium;
        this.cholesterol = cholesterol;
        this.carbohydrates = carbohydrates;
        this.fibre = fibre;
        this.sugar = sugar;
    }

    // Take in Json from the API and convert to Nutrition object
    public static Nutrition createJson(JsonObject o) {
        Nutrition nutrition = new Nutrition();
        nutrition.setName(o.getString("name"));
        nutrition.setCalories(o.getJsonNumber("calories").doubleValue());
        nutrition.setServingSize(o.getJsonNumber("serving_size_g").doubleValue());
        nutrition.setFatTotal(o.getJsonNumber("fat_total_g").doubleValue());
        nutrition.setFatSaturated(o.getJsonNumber("fat_saturated_g").doubleValue());
        nutrition.setProtein(o.getJsonNumber("protein_g").doubleValue());
        nutrition.setSodium(o.getJsonNumber("sodium_mg").doubleValue());
        nutrition.setCholesterol(o.getJsonNumber("cholesterol_mg").doubleValue());
        nutrition.setCarbohydrates(o.getJsonNumber("carbohydrates_total_g").doubleValue());
        nutrition.setFibre(o.getJsonNumber("fiber_g").doubleValue());
        nutrition.setSugar(o.getJsonNumber("sugar_g").doubleValue());

        return nutrition;
    }

    // From the API Json (whole thing), get the needed values, make them to nutrition object, then to list of nutritions
    public static List<Nutrition> create(String json) throws IOException {
        List<Nutrition> nutritions = new LinkedList<>();
        Nutrition nutrition = new Nutrition();
        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader jrd = Json.createReader(is);
            // Read the array
            JsonArray arr = jrd.readArray();

            if (arr != null) {
                for (JsonObject obj : arr.getValuesAs(JsonObject.class)) {
                    nutrition = createJson(obj);
                    nutritions.add(nutrition);
                }
            }
            return nutritions;
        }
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("name", getName())
                .add("calories", getCalories())
                .add("servingSize", getServingSize())
                .add("fatTotal", getFatTotal())
                .add("fatSaturated", getFatSaturated())
                .add("protein", getProtein())
                .add("sodium", getSodium())
                .add("cholesterol", getCholesterol())
                .add("carbohydrates", getCarbohydrates())
                .add("fibre", getFibre())
                .add("sugar", getSugar())
                .build();
    }
}
