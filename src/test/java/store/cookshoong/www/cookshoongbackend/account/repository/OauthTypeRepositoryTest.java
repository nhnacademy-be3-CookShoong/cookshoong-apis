package store.cookshoong.www.cookshoongbackend.account.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthType;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

/**
 * OAuth2 공급자 레포지토리 테스트.
 *
 * @author koesnam (추만석)
 * @since 2023.08.04
 */
@DataJpaTest
@Import({QueryDslConfig.class, TestEntity.class})
class OauthTypeRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    TestEntity testEntity;

    @Autowired
    OauthTypeRepository oauthTypeRepository;

    @Test
    @DisplayName("OAuth2 공급자 조회 - 등록된 공급자일 경우")
    void getReferenceByProvider() {
        OauthType expect = testEntity.getOauthTypePayco();
        em.persist(expect);
        OauthType actual = oauthTypeRepository.getReferenceByProvider("payco");

        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(expect.getId()),
            () -> assertThat(actual.getProvider()).isEqualTo(expect.getProvider())
        );
    }

    @Test
    @DisplayName("OAuth2 공급자 조회 - 등록되지 않은 공급자일 경우")
    void getReferenceByProvider_2() {
        em.persist(testEntity.getOauthTypePayco());

        OauthType actual = oauthTypeRepository.getReferenceByProvider("facebook");

        assertThat(actual).isNull();
    }
}
