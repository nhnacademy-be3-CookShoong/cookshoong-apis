package store.cookshoong.www.cookshoongbackend.account.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountAuthDto;

/**
 * 회원 CRU 를 위한 레포지토리.
 *
 * @author koesnam (추만석)
 * @since 2023.07.04
 */
public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom {
    Optional<SelectAccountAuthDto> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
