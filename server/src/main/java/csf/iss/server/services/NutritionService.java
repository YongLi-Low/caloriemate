package csf.iss.server.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import csf.iss.server.models.Calories;
import csf.iss.server.models.Nutrition;
import csf.iss.server.repository.NutritionRepository;

@Service
public class NutritionService {
    
    @Value("${api.ninjas.key}")
    private String apiNinjasKey;

    public final String url = "https://api.api-ninjas.com/v1/nutrition";

    @Autowired
    private NutritionRepository nutritionRepo;

    // Call API from api-ninjas
    public Optional<List<Nutrition>> getNutritions(@RequestParam String foodName, @RequestParam(required = false, defaultValue = "100") String quantity) {

        List<Nutrition> nutritions = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiNinjasKey);

        String queryParam = quantity != null ? quantity + "g+" + foodName.replaceAll(" ", "+") : foodName;
        // https://api.api-ninjas.com/v1/nutrition?query=300g+fries+and+beef
        // System.out.println(queryParam);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                                        .queryParam("query", queryParam);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

        try {
            nutritions = Nutrition.create(resp.getBody());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (nutritions != null) {
            return Optional.of(nutritions);
        }
        else {
            return Optional.empty();
        }
    }

    // Insert food and calories to caloriestracker SQL
    public void insertCalories(Calories calories) throws SQLException {
        nutritionRepo.insertCalories(calories);
    }

    // Find a list of food and calories from caloriestracker table
    public Optional<List<Calories>> findCalories(String id, String entryDate) {
        return nutritionRepo.findCalories(id, entryDate);
    }

    // Delete calories from the list
    public void deleteCalories(int calId) {
        nutritionRepo.deleteCalories(calId);
    }
}
