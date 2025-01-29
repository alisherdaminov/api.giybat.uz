package api.giybat.uz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSendingService {

    @Value("${spring.mail.username}")
    private String fromAccount;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendRegistrationEmail(String email, Long profileId) {
        String subject = "Registration";
        String body = "Please, click the link to verify your registration:\nhttp://localhost:8080/auth/registration/verification/" + profileId;
        sendEmail(email, subject, body);
    }

    private void sendEmail(String email, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAccount);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);

    }
}
