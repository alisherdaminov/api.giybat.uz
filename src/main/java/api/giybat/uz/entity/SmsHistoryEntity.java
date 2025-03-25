package api.giybat.uz.entity;

import api.giybat.uz.enums.SmsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sms_history")
public class SmsHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "message", columnDefinition = "text")
    private String message;
    @Column(name = "code")
    private String code;
    @Enumerated(EnumType.STRING)
    @Column(name = "smsType_status")
    private SmsType smsTypeStatus;
    @Column(name = "created_date")
    private LocalDateTime created_date;
}
