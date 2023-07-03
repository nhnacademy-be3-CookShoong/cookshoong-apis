package repository;

import store.cookshoong.www.cookshoongbackend.store.StoreBusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoreBusinessHoursRepository extends JpaRepository<StoreBusinessHours, Long>, JpaSpecificationExecutor<StoreBusinessHours> {

}