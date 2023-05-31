package csf.iss.server.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import csf.iss.server.models.Exercise;
import csf.iss.server.services.ExerciseService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;

@Controller
@RequestMapping(path = "/api/exercises", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ExerciseController {
    
    @Autowired
    private ExerciseService exerciseSvc;

    // Get a list of exercises
    @GetMapping
    public ResponseEntity<String> getExercises(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String muscle,
        @RequestParam(required = false) String difficulty
    ) {
        JsonArray result = null;

        Optional<List<Exercise>> arr = this.exerciseSvc.getExercises(name, type, muscle, difficulty);
        List<Exercise> exercises = arr.get();

        JsonArrayBuilder arrBld = Json.createArrayBuilder();

        for (Exercise e: exercises) {
            arrBld.add(e.toJSON());
        }

        result = arrBld.build();

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(result.toString());
    }
}
