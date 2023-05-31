package csf.iss.server.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User() {
    }
    
    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("id", getId())
                .add("username", getUsername())
                .add("email", getEmail())
                .add("password", getPassword())
                .build();
    }
    
}
