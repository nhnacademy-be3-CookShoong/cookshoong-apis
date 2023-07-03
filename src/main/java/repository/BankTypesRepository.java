package repository;

import entity.BankTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BankTypesRepository extends JpaRepository<BankTypes, String>, JpaSpecificationExecutor<BankTypes> {

}