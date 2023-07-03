package repository;

import entity.Charges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargesRepository extends JpaRepository<Charges, String>, JpaSpecificationExecutor<Charges> {

}