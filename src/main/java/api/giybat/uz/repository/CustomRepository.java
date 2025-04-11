package api.giybat.uz.repository;

import api.giybat.uz.dto.post.FilterResultDTO;
import api.giybat.uz.dto.post.PostFilter;
import api.giybat.uz.entity.PostEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CustomRepository {

    @Autowired
    EntityManager entityManager;

    // FilterResultDTO 2 ta entity qaytaradi bular map va count shundan FilterResultDTO yasaldi
    // PostFilter filter, int page, int size bu parametrlar postFilter ni olish uchun filter yoli orqali
    public FilterResultDTO<PostEntity> filterResultDTO(PostFilter filter, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder("where p.visible = true");
        Map<String, Object> params = new HashMap<>();
        if (filter.getQuery() != null) { // filter qilganda queryda data bolsa  quyidagi amal bajarladi if ni ichi
            queryBuilder.append(" and lower(p.title) like :query");
            params.put("query", "%" + filter.getQuery().toLowerCase() + "%");
        }
        // yani created_date desc bu oxiri yaratilgan post boshida chiqadi filter qilganda

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
}
