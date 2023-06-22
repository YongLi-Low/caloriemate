package csf.iss.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthController {
    
    @GetMapping("/oauth/callback")
    public RedirectView handleCallback(@RequestParam("code") String authorizationCode) {
        // Process the authorization code and store it for further use
        // You can save it to a database or use it immediately to obtain the access token
        // For simplicity, let's just print the authorization code
        System.out.println("Authorization code: " + authorizationCode);

        // Redirect to a success page or any other desired location
        return new RedirectView("/success.html");
    }
}
