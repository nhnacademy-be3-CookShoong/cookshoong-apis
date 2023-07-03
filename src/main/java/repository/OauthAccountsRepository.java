package repository;

import entity.OauthAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OauthAccountsRepository extends JpaRepository<OauthAccounts, Long>, JpaSpecificationExecutor<OauthAccounts> {

}