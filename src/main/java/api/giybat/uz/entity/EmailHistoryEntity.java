package api.giybat.uz.entity;

import api.giybat.uz.enums.SmsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "email_history")
public class EmailHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "email")
    private String email;
    @Column(name = "code")
    private String code;
    @Enumerated(EnumType.STRING)
    @Column(name = "emailType_status")
    private SmsType emailTypeStatus;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "attempt_count")
    private Integer attemptCount = 0;
}
