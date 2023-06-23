package csf.iss.server.models;

import com.google.api.client.util.DateTime;

public class EventData {
    private String title;
    private String description;
    private DateTime startDateTime;
    private DateTime endDateTime;
    private String username;
    private String code;
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public DateTime getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(DateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
    public DateTime getEndDateTime() {
        return endDateTime;
    }
    public void setEndDateTime(DateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public EventData(String title, String description, DateTime startDateTime, DateTime endDateTime, String username,
            String code) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.username = username;
        this.code = code;
    }
    
}
