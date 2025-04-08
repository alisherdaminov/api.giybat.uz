package api.giybat.uz.repository;

import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.enums.ProfileRoleEnum;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.List;

@Repository
public interface ProfileRoleRepository extends JpaRepository<ProfileRoleEntity, Long> {

    @Transactional
    @Modifying
    void deleteByProfileId(Integer profileId);



    // bu @Query("select p.roles From ProfileRoleEntity p where p.profileId = ?1") orqali profileId ga tegishli roli olish,
    // yani  List<ProfileRoleEntity> ga kirib har bir rolini olmaslik
    // aynan bizga kerakli roli oladi, u class da boshqa fieldlarni ajratib olilishim shart emas

    @Query("select p.roles From ProfileRoleEntity p where p.profileId = ?1")
    List<ProfileRoleEnum> findAllRolesByProfileId(Integer profileId);
}
