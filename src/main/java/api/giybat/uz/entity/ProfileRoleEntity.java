package api.giybat.uz.entity;

import api.giybat.uz.enums.ProfileRoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "profile_role_entity")
public class ProfileRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "profile_id")
    private Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profileEntity;

    @Column(name = "roles ")
    @Enumerated(EnumType.STRING)
    private ProfileRoleEnum roles;

    @Column(name = "created_date")
    private LocalDateTime createdDate;


}
