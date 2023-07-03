package repository;

import store.cookshoong.www.cookshoongbackend.store.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoresRepository extends JpaRepository<Stores, Long>, JpaSpecificationExecutor<Stores> {

}