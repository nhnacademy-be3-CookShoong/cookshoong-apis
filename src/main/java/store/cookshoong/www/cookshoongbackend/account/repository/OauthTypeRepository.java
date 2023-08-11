package store.cookshoong.www.cookshoongbackend.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthType;

/**
 * 소셜 로그인 제공자 CR 를 위한 레포지토리.
 * (OAuthProvider)
 *
 * @author koesnam
 * @since 2023.07.04
 */
public interface OauthTypeRepository extends JpaRepository<OauthType, Integer> {
    OauthType getReferenceByProvider(String provider);
}
