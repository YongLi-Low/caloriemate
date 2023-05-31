package csf.iss.server.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Calories {
    private Integer id;
    private String foreignId; // id of the user
    private String username;
    private String foodName;
    private double quantity;
    private double calories;
    private String entryDate;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getForeignId() {
        return foreignId;
    }
    public void setForeignId(String foreignId) {
        this.foreignId = foreignId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFoodName() {
        return foodName;
    }
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public double getCalories() {
        return calories;
    }
    public void setCalories(double calories) {
        this.calories = calories;
    }
    public String getEntryDate() {
        return entryDate;
    }
    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    // Convert the calories object (foodName, quantity, calories, entryDate) to JSON to send to angular
    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("id", getId())
                .add("foodName", getFoodName())
                .add("quantity", getQuantity())
                .add("calories", getCalories())
                .add("entryDate", getEntryDate())
                .build();
    }
}
