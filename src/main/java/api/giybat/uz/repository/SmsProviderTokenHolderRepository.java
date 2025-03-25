package api.giybat.uz.repository;

import api.giybat.uz.entity.SmsProviderTokenHolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmsProviderTokenHolderRepository extends JpaRepository<SmsProviderTokenHolderEntity, Integer> {

    Optional<SmsProviderTokenHolderEntity> findTop1By();
}
