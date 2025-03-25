package api.giybat.uz.repository;

import api.giybat.uz.entity.SmsHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SmsHistoryRepository extends JpaRepository<SmsHistoryEntity, String> {

    // select count(*) from SmsHistoryEntity where phoneNumber = ? and created_date between ? and ?
    Long countByPhoneNumberAndCreatedDateBetween(String phoneNumber, LocalDateTime from, LocalDateTime to);
}
