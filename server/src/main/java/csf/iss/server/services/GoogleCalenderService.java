package csf.iss.server.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
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
import java.awt.*;

import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.Browser;
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

    private static final String rootUrl = "http://localhost:4200";
    // private static final String rootUrl = "https://caloriemate.co";

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

    // private static Credential getCredential(final NetHttpTransport HTTP_TRANSPORT, String username) throws IOException {
    //     // Load client secrets
    //     InputStream in = GoogleCalenderService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    //     if (in == null) {
    //         throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    //     }
    //     GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    //     // Build flow and trigger user authorisation request
    //     GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
    //         HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
    //         .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
    //         .setAccessType("offline")
    //         .build();

    //     LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

    //     Browser browser = new Browser() {
    //     @Override
    //     public void browse(String url) throws IOException {
    //         if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
    //             try {
    //                 Desktop.getDesktop().browse(new URI(url));
    //             } catch (URISyntaxException e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     }
    //     };

    //     AuthorizationCodeInstalledApp authCode = null;

    //     if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
    //         authCode = new AuthorizationCodeInstalledApp(flow, receiver, browser);
    //     }

    //     // Credential credential = authCode.authorize("user");
    //     Credential credential = authCode.authorize(username);

    //     // returns an authorized Credential Object
    //     return credential;
    // }

    // public String createEvent(Event event, String username) throws IOException, GeneralSecurityException {
    //     NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    //     Credential credential = getCredential(HTTP_TRANSPORT, username);

    //     // Build the calendar service
    //     Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
    //                         .setApplicationName(APPLICATION_NAME)
    //                         .build();

    //     Event createdEvent = service.events().insert(CALENDAR_ID, event).execute();

    //     String eventLink = createdEvent.getHtmlLink();

    //     return eventLink;
    //     // System.out.println("Event created: " + createdEvent.getHtmlLink());
    // }

    // https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=230416168962-p5emq7hqcq9s3m83ouo43sko2hkapf7l.apps.googleusercontent.com&redirect_uri=http://localhost:8888/Callback&response_type=code&scope=https://www.googleapis.com/auth/calendar.events


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


    // // Here, we're accepting an authorization code as an argument to the getCredential method. This method now tries to exchange the given authorization code for an access token and refresh token, which it saves to a Credential object.
    // private Credential getCredential(String username, String code, String id) throws IOException, GeneralSecurityException {
    //     NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    //     // Load client secrets.
    //     InputStream in = GoogleCalenderService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    //     if (in == null) {
    //         throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    //     }
    //     GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    //     // Build flow.
    //     GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
    //             HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
    //             .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
    //             .setAccessType("offline")
    //             .build();

    //     // Create token response
    //     TokenResponse response = flow.newTokenRequest(code)
    //             .setRedirectUri(rootUrl + "/login/" + username + "/" + id + "/searchexercise") // Replace callbackUrl with your redirect URL.
    //             .execute();

    //     return flow.createAndStoreCredential(response, username);
    // }


    // // rootUrl + "/login/" + username + "/" + id + "/searchexercise"

    // // Here, the createAuthorizationUrl method generates the authorization URL which your front end will direct the user to. The redirectUri is where Google's authorization server will redirect the user after they authorize your app. The state parameter is used to maintain state between your application's request and the redirect, which can help prevent cross-site request forgery. In this case, I've used it to pass along the username, but you can modify this as needed.
    // public String createAuthorizationUrl(String username, String redirectUri) throws IOException, GeneralSecurityException {
    //     NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    //     // Load client secrets.
    //     InputStream in = GoogleCalenderService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    //     if (in == null) {
    //         throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    //     }
    //     GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    //     // Build flow.
    //     GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
    //             HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
    //             .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
    //             .setAccessType("offline")
    //             .build();

    //     // Create authorization URL.
    //     return flow.newAuthorizationUrl().setRedirectUri(redirectUri).setState(username).build();
    // }

    // public String createEvent(Event event, String username, String code, String id) throws IOException, GeneralSecurityException {
    //     NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    //     // Get the credential from the code
    //     Credential credential;
    //     try {
    //         credential = getCredential(username, code, id);
    //     } catch (IOException e) {
    //         // Log error message here
    //         // Redirect user to authorization page
    //         String authorizationUrl = createAuthorizationUrl(username, rootUrl + "/login/" + username + "/" + id + "/searchexercise");
    //         return "redirect:" + authorizationUrl;
    //     }

    //     // Build the calendar service
    //     Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
    //                         .setApplicationName(APPLICATION_NAME)
    //                         .build();

    //     Event createdEvent = service.events().insert(CALENDAR_ID, event).execute();

    //     String eventLink = createdEvent.getHtmlLink();

    //     return eventLink;
    //     // System.out.println("Event created: " + createdEvent.getHtmlLink());
    // }

}
