package csf.iss.server.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.awt.*;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.Browser;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;

@Service
public class GoogleCalenderService {
    
    private static final String APPLICATION_NAME = "CalorieMate";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String CALENDAR_ID = "primary";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);

    private static String getAuthenticationLink(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets
        InputStream in = GoogleCalenderService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorisation request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        // Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(receiver.getRedirectUri()).build();
        
        return authorizationUrl;
        // returns an authorized Credential Object
        // return credential;
    }

    public String getAuthenticationLink() throws IOException, GeneralSecurityException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return getAuthenticationLink(HTTP_TRANSPORT);
    }

    private static Credential getCredential(final NetHttpTransport HTTP_TRANSPORT, String username) throws IOException {
        // Load client secrets
        InputStream in = GoogleCalenderService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorisation request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        
        // LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        // Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        // Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(username);

        Browser browser = new Browser() {
        @Override
        public void browse(String url) throws IOException {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        };

        AuthorizationCodeInstalledApp authCode = null;

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            authCode = new AuthorizationCodeInstalledApp(flow, receiver, browser);
        }

        // Credential credential = authCode.authorize("user");
        Credential credential = authCode.authorize(username);

        // returns an authorized Credential Object
        return credential;
    }

    public String createEvent(Event event, String username) throws IOException, GeneralSecurityException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredential(HTTP_TRANSPORT, username);

        // Build the calendar service
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                            .setApplicationName(APPLICATION_NAME)
                            .build();

        Event createdEvent = service.events().insert(CALENDAR_ID, event).execute();

        String eventLink = createdEvent.getHtmlLink();

        return eventLink;
        // System.out.println("Event created: " + createdEvent.getHtmlLink());
    }

    // https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=230416168962-p5emq7hqcq9s3m83ouo43sko2hkapf7l.apps.googleusercontent.com&redirect_uri=http://localhost:8888/Callback&response_type=code&scope=https://www.googleapis.com/auth/calendar.events

}
