package api.giybat.uz.services.email;

import api.giybat.uz.entity.EmailHistoryEntity;
import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.repository.email.EmailHistoryRepository;
import api.giybat.uz.services.ResourceBundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailHistoryService {

    @Autowired
    private EmailHistoryRepository emailHistoryRepository;
    @Autowired
    private ResourceBundleService resourceBundleService;

    public void createEmailSms(String email, String code, SmsType emailType) {
        EmailHistoryEntity emailHistoryEntity = new EmailHistoryEntity();
        emailHistoryEntity.setEmail(email);
        emailHistoryEntity.setCode(code);
        emailHistoryEntity.setEmailTypeStatus(emailType);
        emailHistoryEntity.setAttemptCount(0);
        emailHistoryEntity.setCreatedDate(LocalDateTime.now());
        emailHistoryRepository.save(emailHistoryEntity);
    }

    public Long getEmailSmsCount(String email) {
        LocalDateTime from = LocalDateTime.now().minusMinutes(1);
        LocalDateTime to = LocalDateTime.now();
        return emailHistoryRepository.countByEmailAndCreatedDateBetween(email, from, to);
    }

    public void checkEmailSmsCode(String email, String code, AppLanguage language) {
        // find last sms by email
        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findFirstByEmailOrderByCreatedDateDesc(email);
        if (optional.isEmpty()) {
            throw new AppBadException(resourceBundleService.getMessage("verification.failed", language));
        }
        // check code
        EmailHistoryEntity emailHistoryEntity = optional.get();
        if (emailHistoryEntity.getAttemptCount() >= 3) {
            throw new AppBadException(resourceBundleService.getMessage("sms.input.attempt.exceeded", language));

        }
        if (!emailHistoryEntity.getCode().equals(code)) {
            emailHistoryRepository.updateAttemptCount(emailHistoryEntity.getId());
            throw new AppBadException(resourceBundleService.getMessage("verification.failed", language));
        }
        // check time
        LocalDateTime expiredDate = LocalDateTime.now().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expiredDate)) { // sms expired
            throw new AppBadException(resourceBundleService.getMessage("sms.code.timeout", language));
        }

    }
}
