package repository;

import store.cookshoong.www.cookshoongbackend.accounts.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountsRepository extends JpaRepository<Accounts, Long>, JpaSpecificationExecutor<Accounts> {

}