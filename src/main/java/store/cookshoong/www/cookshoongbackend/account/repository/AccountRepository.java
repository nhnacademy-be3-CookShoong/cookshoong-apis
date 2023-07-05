package store.cookshoong.www.cookshoongbackend.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;

/**
 * 회원 CRU 를 위한 레포지토리.
 *
 * @author koesnam
 * @since 2023.07.04
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
