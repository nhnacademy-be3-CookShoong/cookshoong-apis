package repository;

import entity.Deliveries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeliveriesRepository extends JpaRepository<Deliveries, String>, JpaSpecificationExecutor<Deliveries> {

}