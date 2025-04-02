package api.giybat.uz.services.email;

import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.services.ResourceBundleService;
import api.giybat.uz.util.JwtUtil;
import api.giybat.uz.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailSendingService {

    @Value("${spring.mail.username}")
    private String fromAccount;

   // @Value("${server.domain}")
    private String serverDomain;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    private ResourceBundleService resourceBundleService;

    public void sendRegistrationEmail(String email, Integer profileId) {
        String subject = "Registration";
        String body = "Please, click the link to verify your registration:\nhttp://localhost:8080/auth/registration/verification/" + profileId;
        body = String.format(body, serverDomain, JwtUtil.encodeVerificationSMS(profileId));
        sendMimeEmail(email, subject, body);
    }


    public void sendResetPasswordEmail(String email, AppLanguage language) {
        // generate code
        String code = RandomUtil.generateRandomNumber();
        String subject = "Reset password Confirmation code: %s";
        String body = "This is your reset password code: " + code;
        checkAndSendMimeEmail(email, subject, body, code, language);
    }

    public void checkAndSendMimeEmail(String email, String subject, String body, String code, AppLanguage language) {
        // check
        Long emailSmsCount = emailHistoryService.getEmailSmsCount(email);
        Integer maxSms = 3;
        if (emailSmsCount >= maxSms) {
            emailHistoryService.createEmailSms(email, code, SmsType.RESET_PASSWORD);
            System.out.println("---- OverAll sms count: " + emailSmsCount + " reached sms limit to this email: " + email + "----");
            throw new AppBadException(resourceBundleService.getMessage("email.sms.count.limit.reached", language));
        }
        // SEND
        sendMimeEmail(email, subject, body);
        // Created
        emailHistoryService.createEmailSms(email, code, SmsType.RESET_PASSWORD);
    }

    public void sendMimeEmail(String email, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setFrom(fromAccount);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            CompletableFuture.runAsync(() -> javaMailSender.send(message));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

//    private void sendSimpleEmail(String email, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(fromAccount);
//        message.setTo(email);
//        message.setSubject(subject);
//        message.setText(body);
//        javaMailSender.send(message);
//
//    }
}
