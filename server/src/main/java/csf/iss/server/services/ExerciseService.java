package csf.iss.server.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import csf.iss.server.models.Exercise;

@Service
public class ExerciseService {
    
    @Value("${api.ninjas.key}")
    private String apiNinjasKey;

    public final String url = "https://api.api-ninjas.com/v1/exercises";

    // Call API from api-ninjas
    public Optional<List<Exercise>> getExercises(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) String type,
                                                @RequestParam(required = false) String muscle,
                                                @RequestParam(required = false) String difficulty
                                                ) {
        List<Exercise> exercises = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiNinjasKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        if (name != null) {
            builder.queryParam("name", name);
        }
        if (type != null) {
            builder.queryParam("type", type);
        }
        if (muscle != null) {
            builder.queryParam("muscle", muscle);
        }
        if (difficulty != null) {
            builder.queryParam("difficulty", difficulty);
        }
        String queryParam = builder.build().encode().toUriString();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(queryParam, HttpMethod.GET, entity, String.class);

        try {
            exercises = Exercise.create(resp.getBody());
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        if (exercises != null) {
            return Optional.of(exercises);
        }
        else {
            return Optional.empty();
        }
    }
        
}
