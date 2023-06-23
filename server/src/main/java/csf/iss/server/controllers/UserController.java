package csf.iss.server.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import csf.iss.server.models.User;
import csf.iss.server.services.UserService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Controller
@RequestMapping(path = "")
public class UserController {

    @Autowired
    private UserService userSvc;
    
    // Registration Form
    @PostMapping(path = "/registration")
    public ResponseEntity<String> isInsertUser(
        @RequestPart String username,
        @RequestPart String email,
        @RequestPart String password,
        @RequestPart String confirmPassword
        ) throws SQLException {
            User user = new User();
            String resp = "";

            user.setId(generateId());
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setConfirmPassword(confirmPassword);

            if (userSvc.checkUsername(user) == false) {
                // no existing username and password typed correctly
                userSvc.insertUser(user);
                String id = user.getId();
                resp = id;  // return the id created to client side (angular)
                userSvc.sendConfirmationEmail(user.getEmail(), user.getUsername());
            }
            else if (userSvc.checkUsername(user)) {
                resp = "Username exists!";
            }

            JsonObject obj = Json.createObjectBuilder()
                            .add("response", resp)
                            .build();

            return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
        }

    public String generateId() {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString().substring(0, 8);
        return id;
    }
    
    // Log in Form
    @PostMapping(path = "/login")
    public ResponseEntity<String> login(
        @RequestPart String username,
        @RequestPart String password
    ) throws SQLException {
        User user = new User(username, password);
        String resp = "";

        if (userSvc.checkUsernamePassword(user) == false) {
            resp = "username and password mismatch";
        }
        else {
            resp = userSvc.getId(username);
        }

        JsonObject obj = Json.createObjectBuilder()
                            .add("response", resp)
                            .build();
                    
        return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
    }

    // Update Profile pic
    @PutMapping(path = "/profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable String id, @RequestParam("thumbnail") MultipartFile thumbnail) {
        String resp = "";

        try {
            userSvc.updateProfile(id, thumbnail);
            resp = "Image updated";
        }
        catch (MaxUploadSizeExceededException e) {
            resp = "Maximum upload size exceeded";
        }
        catch (IOException e) {
            resp = "Failed to upload";
        }

        JsonObject obj = Json.createObjectBuilder()
                            .add("response", resp)
                            .build();
        
        return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
    }

    // Get Profile pic
    @GetMapping(path = "/profile/{id}/getprofile")
    public ResponseEntity<String> getProfile(@PathVariable String id) {
        String resp = "";
        Optional<String> imageOptional = userSvc.getProfileImage(id);
        if (imageOptional.isPresent()) {
            String base64Image = imageOptional.get();
            resp = base64Image;
            // JsonObject obj = Json.createObjectBuilder()
            //             .add("response", resp)
            //             .build();
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }
        else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    // Update BMI
    @PutMapping(path = "/login/{id}/bmi")
    public ResponseEntity<String> updateBmi(@PathVariable String id, @RequestBody Float bmi) {
        String resp = bmi.toString();
        userSvc.updateBmi(id, bmi);

        JsonObject obj = Json.createObjectBuilder()
                        .add("response", resp)
                        .build();
        return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
    }

    // Get BMI
    @GetMapping(path = "/login/{id}/getbmi")
    public ResponseEntity<String> getBmi(@PathVariable String id) {
        String resp = "";
        Optional<Float> bmi = userSvc.getBMI(id);

        if (bmi.isPresent()) {
            resp = bmi.get().toString();
        }
        else {
            resp = "null";
        }

        JsonObject obj = Json.createObjectBuilder()
                        .add("response", resp)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
    }

    // Update Calories
    @PutMapping(path = "/login/{id}/calories")
    public ResponseEntity<String> updateCal(@PathVariable String id, @RequestBody Integer cal) {
        String resp = cal.toString();
        userSvc.updateCal(id, cal);

        JsonObject obj = Json.createObjectBuilder()
                        .add("response", resp)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
    }

    // Get Calories
    @GetMapping(path = "/login/{id}/getcalories")
    public ResponseEntity<String> getCal(@PathVariable String id) {
        String resp = "";
        Optional<Integer> calories = userSvc.getCal(id);

        if (calories.isPresent()) {
            resp = calories.get().toString();
        }
        else {
            resp = "null";
        }

        JsonObject obj = Json.createObjectBuilder()
                        .add("response", resp)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
    }
}
