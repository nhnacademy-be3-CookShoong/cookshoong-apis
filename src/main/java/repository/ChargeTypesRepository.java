package repository;

import entity.ChargeTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargeTypesRepository extends JpaRepository<ChargeTypes, Long>, JpaSpecificationExecutor<ChargeTypes> {

}