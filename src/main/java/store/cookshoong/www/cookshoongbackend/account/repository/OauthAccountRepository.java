package store.cookshoong.www.cookshoongbackend.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthAccount;

/**
 * 소셜 로그인 회원 CR 를 위한 레포지토리.
 *
 * @author koesnam
 * @since 2023.07.04
 */
public interface OauthAccountRepository extends JpaRepository<OauthAccount, OauthAccount.Pk> {
}
