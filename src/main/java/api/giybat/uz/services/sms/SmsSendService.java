package api.giybat.uz.services.sms;

import api.giybat.uz.dto.sms.SmsAuthDTO;
import api.giybat.uz.dto.sms.SmsAuthResponseDTO;
import api.giybat.uz.dto.sms.SmsRequestDTO;
import api.giybat.uz.dto.sms.SmsSendResponseDTO;
import api.giybat.uz.entity.SmsProviderTokenHolderEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.repository.sms.SmsProviderTokenHolderRepository;
import api.giybat.uz.services.ResourceBundleService;
import api.giybat.uz.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SmsSendService {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${sms.url}")
    private String baseUrl;
    @Value("${sms.email}")
    private String accountLogin;
    @Value("${sms.password}")
    private String accountPassword;
    @Autowired
    private SmsProviderTokenHolderRepository smsProviderTokenHolderRepository;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private ResourceBundleService resourceBundleService;


    public void sendRegistrationSms(String phoneNumber) {
        String code = RandomUtil.generateRandomNumber();
        String message = "Giybat.uz da ro'yxatdan o'tdingiz. Kod: %s";
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.REGISTRATION);
    }

    public void sendResetPassword(String phoneNumber, AppLanguage language) {
        String code = RandomUtil.generateRandomNumber();
        String message = resourceBundleService.getMessage("reset.password.message", language);
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.RESET_PASSWORD);
    }

    public void sendPhoneUsernameChangeConfirmationSms(String phoneNumber, AppLanguage language) {
        String code = RandomUtil.generateRandomNumber();
        String message = resourceBundleService.getMessage("sms.code.username.change", language);
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.USERNAME_UPDATE);
    }

    private SmsSendResponseDTO sendSms(String phoneNumber, String message, String code, SmsType smsType) {
        // check
        Long smsCount = smsHistoryService.getPhoneNumberSmsCount(phoneNumber);
        Integer maxSms = 3;
        if (smsCount >= maxSms) {
            System.out.println("---- OverAll sms count: " + smsCount + " reached sms limit to this phone number: " + phoneNumber + "----");
            throw new AppBadException("Sms count limit reached");
        }
        // SEND
        SmsSendResponseDTO result = sendSms(phoneNumber, message);
        // SAVE
        smsHistoryService.createPhoneNumberSms(phoneNumber, message, code, smsType);
        return result;
    }

    private SmsSendResponseDTO sendSms(String phoneNumber, String message) {
        //get token
        String token = getToken();
        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        //body
        SmsRequestDTO body = new SmsRequestDTO();
        body.setFrom("4546");
        body.setMessage(message);
        body.setPhoneNumber(phoneNumber);

        // request
        HttpEntity<SmsRequestDTO> httpEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<SmsSendResponseDTO> response = restTemplate.exchange(
                    baseUrl + "/message/sms/send",
                    HttpMethod.POST,
                    httpEntity,
                    SmsSendResponseDTO.class);
            return response.getBody();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private String getToken() {
        Optional<SmsProviderTokenHolderEntity> optional = smsProviderTokenHolderRepository.findTop1By();
        // agar token bolmasa optional.isEmpty() da, tokenni olib olsin bu shartda
        if (optional.isEmpty()) { // if token not found
            String token = getTokenFromProvider();
            SmsProviderTokenHolderEntity entity = new SmsProviderTokenHolderEntity();
            entity.setToken(token);
            entity.setCreated_date(LocalDateTime.now());
            entity.setExpired_date(LocalDateTime.now().plusMonths(1));
            smsProviderTokenHolderRepository.save(entity);
            return token;
        }
        // LocalDateTime.now().isBefore(entity.getExpired_date())
        // hozirgi vaqt kichik bolsa shu expireDate dan
        SmsProviderTokenHolderEntity entity = new SmsProviderTokenHolderEntity();
        if (LocalDateTime.now().isBefore(entity.getExpired_date())) { // if token not expired
            return entity.getToken();
        }
        // get new token and update
        String token = getTokenFromProvider();
        entity.setToken(token);
        entity.setCreated_date(LocalDateTime.now());
        entity.setExpired_date(LocalDateTime.now().plusMonths(1));
        smsProviderTokenHolderRepository.save(entity);
        return token;
    }

    private String getTokenFromProvider() {
        SmsAuthDTO smsAuthDTO = new SmsAuthDTO();
        smsAuthDTO.setEmail(accountLogin);
        smsAuthDTO.setPassword(accountPassword);
        try {
            SmsAuthResponseDTO response = restTemplate.postForObject(baseUrl + "/auth/token", smsAuthDTO, SmsAuthResponseDTO.class);
            System.out.println(response.getData().getToken());
            return response.getData().getToken();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
