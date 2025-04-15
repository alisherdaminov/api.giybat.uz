package api.giybat.uz.repository;

import api.giybat.uz.dto.post.FilterResultDTO;
import api.giybat.uz.dto.post.PostAdminFilterDTO;
import api.giybat.uz.dto.post.PostPublicFilterDTO;
import api.giybat.uz.entity.PostEntity;
import api.giybat.uz.mapper.PostDetailMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CustomPostRepository {

    @Autowired
    EntityManager entityManager;

    // FilterResultDTO 2 ta entity qaytaradi bular map va count shundan FilterResultDTO yasaldi
    // PostPublicFilterDTO filter, int page, int size bu parametrlar postFilter ni olish uchun filter yoli orqali
    public FilterResultDTO<PostEntity> filterResultPublicDTO(PostPublicFilterDTO filter, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder("where p.visible = true ");
        Map<String, Object> params = new HashMap<>();
        if (filter.getQuery() != null) { // filter qilganda queryda data bolsa  quyidagi amal bajarladi if ni ichi
            queryBuilder.append(" and lower(p.title) like :query");
            params.put("query", "%" + filter.getQuery().toLowerCase() + "%");
        }
        // select * from PostEntity where visible = true order by created_date desc
        StringBuilder selectBuilder = new StringBuilder("Select p From PostEntity p ")
                .append(queryBuilder)
                .append(" order by p.createdDate desc");
        Query selectQuery = entityManager.createQuery(selectBuilder.toString());
        selectQuery.setFirstResult((page) * size); //offset 50
        selectQuery.setMaxResults(size); //limit 30
        params.forEach(selectQuery::setParameter); // map
        List<PostEntity> postEntities = selectQuery.getResultList();// bizga postlarni olib keladi

        // select count(*) from PostEntity where visible = true
        StringBuilder countBuilder = new StringBuilder("Select count(p) From PostEntity p ").append(queryBuilder);
        Query countQuery = entityManager.createQuery(countBuilder.toString());
        params.forEach(countQuery::setParameter);// map
        Long count = (Long) countQuery.getSingleResult(); // bizga count ni olib keladi
        return new FilterResultDTO<>(postEntities, count);
    }

    // original
    public FilterResultDTO<Object[]> filterResultAdminDTO(PostAdminFilterDTO filter, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder("where p.visible = true ");
        Map<String, Object> params = new HashMap<>();

        if (filter.getProfileQuery() != null) {
            queryBuilder.append(" and (lower(pr.name) like :profileQuery or lower(pr.username) like :profileQuery) ");
            params.put("profileQuery", "%" + filter.getProfileQuery().toLowerCase() + "%");
        }

        if (filter.getPostQuery() != null) {
            queryBuilder.append(" and (lower(p.title) like :postQuery or p.id = :postId) ");
            params.put("postQuery", "%" + filter.getPostQuery().toLowerCase() + "%");
            params.put("postId", filter.getPostQuery().toLowerCase());
        }

        // SELECT
        StringBuilder selectBuilder = new StringBuilder("Select p.id as postId, p.title as postTitle, " +
                "p.photoId as postPhotoId, p.createdDate as postCreatedDate, " +
                "pr.id as profileId, pr.name as profileName, pr.username as profileUsername ")
                .append(" from PostEntity as p ")
                .append(" inner join p.profileEntity pr ")
                .append(queryBuilder)
                .append(" order by p.createdDate desc ");
        Query selectQuery = entityManager.createQuery(selectBuilder.toString());
        selectQuery.setFirstResult(page * size);
        selectQuery.setMaxResults(size);
        params.forEach(selectQuery::setParameter);
        List<Object[]> postEntities = selectQuery.getResultList();

        // COUNT
        StringBuilder countBuilder = new StringBuilder("Select count(p) From PostEntity p inner join p.profileEntity pr ")
                .append(queryBuilder);
        Query countQuery = entityManager.createQuery(countBuilder.toString());
        params.forEach(countQuery::setParameter);
        Long count = (Long) countQuery.getSingleResult();

        return new FilterResultDTO<>(postEntities, count);
    }

}
