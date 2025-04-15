package api.giybat.uz.repository;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.mapper.ProfileDetailMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long>, PagingAndSortingRepository<ProfileEntity, Long> {

    // select * from ProfileEntity where username =? and visible = true;
    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);

    Optional<ProfileEntity> findByIdAndVisibleTrue(Integer id);

    // bu querda profiEntity ni olib keladi fieldlar bilan va userni postlar count ni olib keladi, roles ni 1 ta linega olib keladi (ROLE_ADMIN, ROLE_USER) KABI
    @Query(value = " select p.id as id, p.name as name, p.username as username, p.photo_id as photoId, p.status as status, p.created_date as createdDate, " +
            "(select count(*) from post as pt where pt.profile_id = p.id) as postCount, " +
            "(select string_agg(pr.roles, ', ') from profile_role_entity as pr where pr.profile_id = p.id)" +
            " from profile as p" +
            " where p.visible = true order by p.created_date desc",
            nativeQuery = true,
            countQuery = "select count(*) from profile where visible = true")
    Page<ProfileDetailMapper> filterByVisibleAsInnerJoinWithSubQuery(PageRequest pageRequest);

    // @Query("SELECT p FROM ProfileEntity as p inner join fetch p.roleList WHERE (LOWER(p.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND p.visible = true order by p.createdDate desc")
    @Query(value = " select p.id as id, p.name as name, p.username as username, p.photo_id as photoId, p.status as status, p.created_date as createdDate, " +
            "(select count(*) from post as pt where pt.profile_id = p.id) as postCount, " +
            "(select string_agg(pr.roles, ', ') from profile_role_entity as pr where pr.profile_id = p.id)" +
            " from profile as p" +
            " where (LOWER(p.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))) and p.visible = true order by p.created_date desc",
            nativeQuery = true,
            countQuery = "select count(*) from profile as p where (LOWER(p.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))) and visible = true")
    Page<ProfileDetailMapper> filterByUsernameAndNameAsInnerJoin(@Param("query") String query, Pageable pageable);

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

    @Transactional
    @Modifying
    @Query("update ProfileEntity set name =?2 where id = ?1")
    void updateDetail(Integer id, String name);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set tempUsername =?2 where id = ?1")
    void updateTempUsername(Integer id, String tempUsername);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set username =?2 where id = ?1")
    void updateUsername(Integer id, String username);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set photoId =?2 where id = ?1")
    void updatePhoto(Integer id, String photoId);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set visible = false where id = ?1")
    void deleteAsVisibleFalse(Integer id);
}


//    @Query(""" in postgresSQL DA bu query ishlaydi
//            select p.id as id, p.name as name, p.username as username, p.photo_id as photoId, p.status as status, p.created_date as createdDate,
//            (select count(*) from post where profile_id = p.id) as postCount,
//            (select string_agg(roles, ', ') from profile_role_entity where profile_id = p.id)
//            from profile p""")