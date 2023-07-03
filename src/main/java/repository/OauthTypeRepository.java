package repository;

import entity.OauthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OauthTypeRepository extends JpaRepository<OauthType, Integer>, JpaSpecificationExecutor<OauthType> {

}