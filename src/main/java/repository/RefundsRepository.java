package repository;

import entity.Refunds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RefundsRepository extends JpaRepository<Refunds, String>, JpaSpecificationExecutor<Refunds> {

}