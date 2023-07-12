package store.cookshoong.www.cookshoongbackend.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;

/**
 * 회원상태 CRUD 를 위한 레포지토리.
 *
 * @author koesnam
 * @since 2023.07.04
 */
public interface AccountStatusRepository extends JpaRepository<AccountStatus, String> {
}
