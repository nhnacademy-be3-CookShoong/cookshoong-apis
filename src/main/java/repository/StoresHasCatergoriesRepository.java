package repository;

import entity.StoresHasCatergories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoresHasCatergoriesRepository extends JpaRepository<StoresHasCatergories, String>, JpaSpecificationExecutor<StoresHasCatergories> {

}