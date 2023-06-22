package csf.iss.server.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import csf.iss.server.models.EventData;
import csf.iss.server.models.Exercise;
import csf.iss.server.services.ExerciseService;
import csf.iss.server.services.GoogleCalenderService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Controller
@RequestMapping(path = "/api/exercises", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ExerciseController {
    
    @Autowired
    private ExerciseService exerciseSvc;

    @Autowired
    private GoogleCalenderService calendarSvc;

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

    @PostMapping(path = "/events")
    public ResponseEntity<String> insertEvent(@RequestBody EventData eventData) throws IOException, GeneralSecurityException {
        try{
            String title = eventData.getTitle();
            String description = eventData.getDescription();
            DateTime startDateTime = eventData.getStartDateTime();
            DateTime endDateTime = eventData.getEndDateTime();
            String username = eventData.getUsername();

            Event event = new Event()
                        .setSummary(title)
                        .setDescription(description);
            
            EventDateTime start = new EventDateTime().setDateTime(startDateTime);
            EventDateTime end = new EventDateTime().setDateTime(endDateTime);
            event.setStart(start);
            event.setEnd(end);

            calendarSvc.createEvent(event, username);

            JsonObject obj = Json.createObjectBuilder()
                                .add("response", "Event created successfully")
                                .build();

            return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
        }
        catch(IOException | GeneralSecurityException e) {
            JsonObject obj = Json.createObjectBuilder()
                .add("response", "Failed to create event: " + e.getMessage())
                .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj.toString());
        }
        
    }

    @GetMapping(path = "/authenticationlink")
    public ResponseEntity<String> getAuthenticationLink() throws IOException, GeneralSecurityException {
        String authenticationLink = calendarSvc.getAuthenticationLink();
        JsonObject obj = Json.createObjectBuilder()
                        .add("response", authenticationLink)
                        .build();
        
        System.out.println(obj.toString());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(obj.toString());
    }
}
