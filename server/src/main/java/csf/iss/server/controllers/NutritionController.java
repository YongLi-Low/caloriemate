package csf.iss.server.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import csf.iss.server.models.Calories;
import csf.iss.server.models.Nutrition;
import csf.iss.server.services.NutritionService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Controller
@RequestMapping(path = "/api/nutrition", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class NutritionController {
    
    @Autowired
    private NutritionService nutritionSvc;

    // Get a list of food nutrition values
    @GetMapping
    public ResponseEntity<String> getNutritions(@RequestParam String foodName, @RequestParam(required = false, defaultValue = "100") String quantity) {
        
        JsonArray result = null;

        Optional<List<Nutrition>> arr = this.nutritionSvc.getNutritions(foodName, quantity);
        List<Nutrition> nutritions = arr.get();

        JsonArrayBuilder arrBlb = Json.createArrayBuilder();

        for (Nutrition n : nutritions) {
            arrBlb.add(n.toJSON());
        }

        result = arrBlb.build();

        // System.out.println(result.toString());
        // [{"name":"fries","calories":953.2,"servingSize":300.0,"fatTotal":44.3,"fatSaturated":7.0,"protein":10.2,"sodium":638.0,"cholesterol":0.0,"carbohydrates":123.3,"fibre":11.5,"sugar":0.9},
        // {"name":"beef","calories":291.9,"servingSize":100.0,"fatTotal":19.7,"fatSaturated":7.8,"protein":26.6,"sodium":63.0,"cholesterol":87.0,"carbohydrates":0.0,"fibre":0.0,"sugar":0.0}]

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(result.toString());
    }

    // Insert the food calories data into caloriestracker table
    @PostMapping(path = "/insertfoodcalories")
    public ResponseEntity<String> insertCalories(@RequestBody Calories calories) {
        String resp = "";
        try {
            nutritionSvc.insertCalories(calories);
            resp = "Success";
        }
        catch (Exception e) {
            resp = "Fail";
        }

        JsonObject obj = Json.createObjectBuilder()
                            .add("response", resp)
                            .build();

        return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
    }
    
    // Find the array of food and calories consumed in a particular date from caloriestracker
    @GetMapping(path = "/findfoodcalories")
    public ResponseEntity<String> findCalories(@RequestParam String id, @RequestParam String entryDate) {

        JsonArray result = null;

        Optional<List<Calories>> arr = nutritionSvc.findCalories(id, entryDate);
        List<Calories> caloriesList = arr.get();

        JsonArrayBuilder arrBld = Json.createArrayBuilder();

        for (Calories c: caloriesList) {
            arrBld.add(c.toJSON());
        }

        result = arrBld.build();

        return ResponseEntity.status(HttpStatus.OK).body(result.toString());
    }

    // Delete the food and calories from the user's list
    @DeleteMapping(path = "/deletefoodcalories")
    public ResponseEntity<String> deleteCalories(@RequestParam("id") int calId) {
        // /deletefoodcalories?id=1
        String resp = "";

        nutritionSvc.deleteCalories(calId);

        resp = "deleted";

        JsonObject obj = Json.createObjectBuilder()
                            .add("response", resp)
                            .build();

        return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
    }
}
