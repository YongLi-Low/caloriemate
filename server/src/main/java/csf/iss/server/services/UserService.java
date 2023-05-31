package csf.iss.server.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import csf.iss.server.models.User;
import csf.iss.server.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

    // For registration
    public void insertUser(User user) throws SQLException {
        userRepo.insertUser(user);
    }

    public boolean checkUsername(User user) {
        return userRepo.checkUsername(user);
    }

    // For login
    public boolean checkUsernamePassword(User user) {
        return userRepo.checkUsernamePassword(user);
    }

    public String getId(String username) {
        return userRepo.getId(username);
    }

    // Update BMI
    public void updateBmi(String id, Float bmi) {
        userRepo.updateBmi(id, bmi);
    }

    // Get BMI
    public Optional<Float> getBMI(String id) {
        return userRepo.getBMI(id);
    }

    // Update Calories
    public void updateCal(String id, Integer cal) {
        userRepo.updateCal(id, cal);
    }

    // Get Calories
    public Optional<Integer> getCal(String id) {
        return userRepo.getCal(id);
    }

    // Sending confirmation email after registration
    public void sendConfirmationEmail(String email, String username) {
        SendGrid sg = new SendGrid("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");
        Request request = new Request();

        try {
            Email from = new Email("corbel-phoenix0b@icloud.com");
            String subject = "New Registration";
            Email to = new Email(email);
            Content content = new Content("text/plain", "Hi there " + username + "! Thank you for registering an account with us!");

            Mail mail = new Mail(from, subject, to, content);

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Confirmation email sent! Status code: " + response.getStatusCode());
        }
        catch(IOException e) {
            e.printStackTrace();
            System.out.println("Failed to send confirmation email.");
        }
    }
}