package api.giybat.uz.repository;

import api.giybat.uz.entity.PostEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, String>, PagingAndSortingRepository<PostEntity, String> {

    // select * from Post where profile_id = ? and visible = true order by createdDate desc
    Page<PostEntity> findAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(Integer profileId, Pageable pageable);

    // in one pages below 3 posts query
    @Query("select p from PostEntity p where p.id != ?1 and p.visible = true order by p.createdDate desc limit 3")
    List<PostEntity> getSimilarPostList(String exceptedPostId);

    @Transactional
    @Modifying
    @Query("update PostEntity set visible = false where id = ?1")
    void deleteByVisibleFalse(String id);
}
