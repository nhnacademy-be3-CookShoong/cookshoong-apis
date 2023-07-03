package repository;

import entity.DayCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DayCodeRepository extends JpaRepository<DayCode, String>, JpaSpecificationExecutor<DayCode> {

}