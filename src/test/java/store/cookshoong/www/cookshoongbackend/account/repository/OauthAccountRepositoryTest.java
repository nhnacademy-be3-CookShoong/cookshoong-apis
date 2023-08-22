package store.cookshoong.www.cookshoongbackend.account.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaSystemException;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthAccount;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthType;
import store.cookshoong.www.cookshoongbackend.common.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * OAuth2 유저 저장에 대한 테스트.
 *
 * @author koesnam (추만석)
 * @since 2023.08.04
 */
@DataJpaTest
@Import({QueryDslConfig.class, TestPersistEntity.class})
class OauthAccountRepositoryTest {
    @Autowired
    TestEntityManager em;

    @Autowired
    TestPersistEntity testPersistEntity;

    @Autowired
    TestEntity testEntity;

    @Autowired
    OauthAccountRepository oauthAccountRepository;

    @Test
    @DisplayName("OAuth2 회원 저장")
    void save() {
        Account account = testPersistEntity.getLevelOneActiveCustomer();
        OauthType oauthType = testPersistEntity.getPaycoType();
        OauthAccount expect = new OauthAccount(account, oauthType, "temp-account-code");

        oauthAccountRepository.save(expect);

        OauthAccount actual = em.find(OauthAccount.class, expect.getPk());

        assertAll(
            () -> assertEquals(actual.getAccount().getId(), expect.getAccount().getId()),
            () -> assertEquals(actual.getOauthType().getId(), expect.getOauthType().getId()),
            () -> assertEquals(actual.getAccountCode(), expect.getAccountCode())
        );
    }

    @Test
    @DisplayName("OAuth2 회원 저장 - 필수 필드 누락")
    void save_2() {
        OauthAccount expect = ReflectionUtils.newInstance(OauthAccount.class);

        assertThatThrownBy(
            () -> oauthAccountRepository.save(expect))
            .isInstanceOf(JpaSystemException.class);
    }
}
