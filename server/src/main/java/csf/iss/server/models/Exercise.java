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

public class Exercise {
    private String name;
    private String type;
    private String muscle;
    private String equipment;
    private String difficulty;
    private String instructions;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getMuscle() {
        return muscle;
    }
    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }
    public String getEquipment() {
        return equipment;
    }
    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String dificulty) {
        this.difficulty = dificulty;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    // Take in JSON from the API and convert to Exercise object
    public static Exercise createJson(JsonObject o) {
        Exercise exercise = new Exercise();
        exercise.setName(o.getString("name"));
        exercise.setType(o.getString("type"));
        exercise.setMuscle(o.getString("muscle"));
        exercise.setEquipment(o.getString("equipment"));
        exercise.setDifficulty(o.getString("difficulty"));
        exercise.setInstructions(o.getString("instructions"));

        return exercise;
    }

    // From the API JSON (whole thing), get the needed values, make them to Exercise object, then to a list of Exercises
    public static List<Exercise> create(String json) throws IOException {
        List<Exercise> exercises = new LinkedList<>();
        Exercise exercise = new Exercise();
        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader jrd = Json.createReader(is);
            // Read the array
            JsonArray arr = jrd.readArray();

            if (arr != null) {
                for (JsonObject obj : arr.getValuesAs(JsonObject.class)) {
                    exercise = createJson(obj);
                    exercises.add(exercise);
                }
            }
            return exercises;
        }
    }
    
    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("name", getName())
                .add("type", getType())
                .add("muscle", getMuscle())
                .add("equipment", getEquipment())
                .add("difficulty", getDifficulty())
                .add("instructions", getInstructions())
                .build();
    }
}
