package store.cookshoong.www.cookshoongbackend.account.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountAuthDto;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountStatusDto;

/**
 * 회원 CRU 를 위한 레포지토리.
 *
 * @author koesnam (추만석)
 * @since 2023.07.04
 */
public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom {
    /**
     * 로그인 아이디 기준으로 회원의 자격증명정보와 부가정보를 조회하기위한 메서드.
     *
     * @param loginId the login id
     * @return the optional
     */
    Optional<SelectAccountAuthDto> findByLoginId(String loginId);

    /**
     * 회원 시퀀스 기준으로 회원의 현재 상태정보를 조회하기위한 메서드.
     *
     * @param accountId the account id
     * @return the optional
     */
    Optional<SelectAccountStatusDto> findAccountStatusById(Long accountId);

    Optional<Account> findAccountById(Long accountId);

    /**
     * 로그인 아이디의 중복을 확인하기 위한 메서드.
     *
     * @param loginId the login id
     * @return the boolean
     */
    boolean existsByLoginId(String loginId);
}
