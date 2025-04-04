package api.giybat.uz.entity;

import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.GeneralStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "profile")
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "username")
    private String username;
    @Column(name = "temp_username")
    private String tempUsername;
    @Column(name = "password")
    private String password;

    // User - ACTIVE,BLOCK
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GeneralStatus status;
    // if I add extra fields in class there will not be saved in database and get 500 error
    // there are always 2 ways to fix and add extra fields in database
    // 1-usul: application.properties da updateni create qilib table ni yangilash bunda xamma data lar ochadi
    // 2-usul: Hamma datalarni ochirmasdan, postgresdan tabledan ochirish usulu
    // This - SELECT * FROM profile;
    //alter table profile
    //drop constraint profile_status_check;
    // va refresh table!!

    //User registered not fully, once we can make him visible or invisible
    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "created_date")
    private LocalDateTime created_date;

//    @OneToMany(mappedBy = "profileEntity ")
//    private List<ProfileRoleEntity> roleList;
}
