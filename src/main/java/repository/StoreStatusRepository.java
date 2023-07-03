package repository;

import entity.StoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoreStatusRepository extends JpaRepository<StoreStatus, String>, JpaSpecificationExecutor<StoreStatus> {

}