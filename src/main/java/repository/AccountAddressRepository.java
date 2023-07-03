package repository;

import entity.AccountAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountAddressRepository extends JpaRepository<AccountAddress, Long>, JpaSpecificationExecutor<AccountAddress> {

}