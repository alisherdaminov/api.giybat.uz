package api.giybat.uz.repository;

import api.giybat.uz.entity.PostEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, String> {

    // select * from Post where profile_id = ? and visible = true
    List<PostEntity> findAllByProfileIdAndVisibleTrue(Integer profileId);

    @Transactional
    @Modifying
    @Query("update PostEntity set visible = false where id = ?1")
    void deleteByVisibleFalse(String id);
}
