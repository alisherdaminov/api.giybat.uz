package api.giybat.uz.repository.sms;

import api.giybat.uz.entity.SmsHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SmsHistoryRepository extends JpaRepository<SmsHistoryEntity, String> {

    // select count(*) from SmsHistoryEntity where phoneNumber = ? and created_date between ? and ?
    Long countByPhoneNumberAndCreatedDateBetween(String phoneNumber, LocalDateTime from, LocalDateTime to);

    // select * from SmsHistoryEntity where phoneNumber = ? order by created_date desc limit 1
    Optional<SmsHistoryEntity> findFirstByPhoneNumberOrderByCreatedDateDesc(String phoneNumber);


    // coalesce is for null check, if null then 0, if not null then add 1, if null then 2
    @Modifying
    @Transactional
    @Query("update SmsHistoryEntity set attemptCount = coalesce(attemptCount, 0) + 1 where id = ?1")
    void updateAttemptCount(String id);
}
