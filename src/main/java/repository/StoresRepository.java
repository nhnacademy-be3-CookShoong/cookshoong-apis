package repository;

import entity.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoresRepository extends JpaRepository<Stores, Long>, JpaSpecificationExecutor<Stores> {

}