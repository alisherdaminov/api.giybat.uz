package api.giybat.uz.services.sms;

import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.repository.SmsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SmsHistoryService {

    @Autowired
    private SmsHistoryRepository smsHistoryRepository;

    public void create(String phoneNumber, String message, String code, SmsType smsType) {
        SmsHistoryEntity smsHistoryEntity = new SmsHistoryEntity();
        smsHistoryEntity.setPhoneNumber(phoneNumber);
        smsHistoryEntity.setMessage(message);
        smsHistoryEntity.setCode(code);
        smsHistoryEntity.setSmsTypeStatus(smsType);
        smsHistoryEntity.setCreated_date(LocalDateTime.now());
        smsHistoryRepository.save(smsHistoryEntity);
    }

    public Long getSmsCount(String phoneNumber) {
        LocalDateTime from = LocalDateTime.now().minusMinutes(1);
        LocalDateTime to = LocalDateTime.now();
        return smsHistoryRepository.countByPhoneNumberAndCreatedDateBetween(phoneNumber, from, to);
    }
}
