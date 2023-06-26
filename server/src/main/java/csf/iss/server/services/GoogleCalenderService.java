package csf.iss.server.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
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

    // private static final String rootUrl = "http://localhost:4200";
    private static final String rootUrl = "https://caloriemate.co";

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

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String username, String code)
        throws IOException, URISyntaxException {

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
            
            Credential credential = flow.loadCredential(username);

            if (credential == null) {
                GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(rootUrl)
                    .execute();
                
                credential = flow.createAndStoreCredential(tokenResponse, username);
            }

            return credential;
        }

    public Optional<String> getAuthorizationUrl(String username, String id, String eventDataJson) 
        throws IOException, GeneralSecurityException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = GoogleCalenderService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        Credential credential = flow.loadCredential(username);

        if (credential == null) {

            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<HashMap<String,String>> typeRef = new TypeReference<HashMap<String,String>>() {};
            Map<String, String> eventData = objectMapper.readValue(eventDataJson, typeRef);
            Map<String, String> stateData = new HashMap<>();
            stateData.put("username", username);
            stateData.put("id", id);
            stateData.putAll(eventData);

            // Convert to JSON string
            String stateJson = objectMapper.writeValueAsString(stateData);

            // Encode to base64 for URL safety
            String stateString = Base64.getUrlEncoder().encodeToString(stateJson.getBytes(StandardCharsets.UTF_8));

            String url = flow.newAuthorizationUrl()
                .setAccessType("offline")
                .setClientId(clientSecrets.getDetails().getClientId())
                .setRedirectUri(rootUrl)
                .setResponseTypes(Arrays.asList("code"))
                .setScopes(SCOPES)
                .setState(stateString)
                .toString();

            return Optional.of(url);
        }
        return Optional.empty();
    }

    public void createEvent(Event event, String username, String code)
        throws IOException, GeneralSecurityException, URISyntaxException {
            // Builde a new authorized API Client service
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, username, code))
                .setApplicationName(APPLICATION_NAME)
                .build();

            service.events().insert(CALENDAR_ID, event).execute();
        }


}
