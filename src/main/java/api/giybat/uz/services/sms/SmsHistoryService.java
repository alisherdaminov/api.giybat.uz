package api.giybat.uz.services.sms;

import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.repository.sms.SmsHistoryRepository;
import api.giybat.uz.services.ResourceBundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SmsHistoryService {

    @Autowired
    private SmsHistoryRepository smsHistoryRepository;
    @Autowired
    private ResourceBundleService resourceBundleService;

    public void createPhoneNumberSms(String phoneNumber, String message, String code, SmsType smsType) {
        SmsHistoryEntity smsHistoryEntity = new SmsHistoryEntity();
        smsHistoryEntity.setPhoneNumber(phoneNumber);
        smsHistoryEntity.setMessage(message);
        smsHistoryEntity.setCode(code);
        smsHistoryEntity.setSmsTypeStatus(smsType);
        smsHistoryEntity.setAttemptCount(0);
        smsHistoryEntity.setCreatedDate(LocalDateTime.now());
        smsHistoryRepository.save(smsHistoryEntity);
    }

    public Long getPhoneNumberSmsCount(String phoneNumber) {
        LocalDateTime from = LocalDateTime.now().minusMinutes(1);
        LocalDateTime to = LocalDateTime.now();
        return smsHistoryRepository.countByPhoneNumberAndCreatedDateBetween(phoneNumber, from, to);
    }

    public void checkPhoneNumberSmsCode(String phoneNumber, String code, AppLanguage language) {
        // find last sms by phoneNumber
        Optional<SmsHistoryEntity> optional = smsHistoryRepository.findFirstByPhoneNumberOrderByCreatedDateDesc(phoneNumber);
        if (optional.isEmpty()) {
            throw new AppBadException(resourceBundleService.getMessage("verification.failed", language));
        }
        // check code
        SmsHistoryEntity smsHistoryEntity = optional.get();
        if (smsHistoryEntity.getAttemptCount() >= 3) {
            throw new AppBadException(resourceBundleService.getMessage("sms.input.attempt.exceeded", language));

        }
        if (!smsHistoryEntity.getCode().equals(code)) {
            smsHistoryRepository.updateAttemptCount(smsHistoryEntity.getId());
            throw new AppBadException(resourceBundleService.getMessage("verification.failed", language));
        }
        // check time
        LocalDateTime expiredDate = LocalDateTime.now().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expiredDate)) { // sms expired
            throw new AppBadException(resourceBundleService.getMessage("sms.code.timeout", language));
        }

    }
}
