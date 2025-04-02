package api.giybat.uz.repository;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    // select * from ProfileEntity where username =? and visible = true;
    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);

    Optional<ProfileEntity> findByIdAndVisibleTrue(Integer id);


    // update ProfileEntity set status =?2 where id = ?1. is user in registration or not
    @Transactional
    @Modifying
    @Query("update ProfileEntity set status =?2 where id = ?1")
    void changeStatus(Integer id, GeneralStatus status);

    // update ProfileEntity set password =?2 where id = ?1. is user update reset password
    @Transactional
    @Modifying
    @Query("update ProfileEntity set password =?2 where id = ?1")
    void updatePassword(Integer id, String password);
}
