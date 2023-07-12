package store.cookshoong.www.cookshoongbackend.store.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.cookshoong.www.cookshoongbackend.store.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.store.repository.merchant.MerchantRepository;

/**
 * 가맹점 레포지토리 테스트코드 작성.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
@DataJpaTest
class MerchantRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    TestEntityManager em;
    @Autowired
    MerchantRepository merchantRepository;

    @Test
    @DisplayName("가맹점 조회 - 성공")
    void select_merchant() {
        Merchant actual = new Merchant("네네치킨");

        merchantRepository.save(actual);

        Merchant expect = merchantRepository.findMerchantByName(actual.getName()).orElseThrow();
        assertThat(expect.getId()).isEqualTo(actual.getId());
        assertThat(expect.getName()).isEqualTo(actual.getName());
    }

    @Test
    @DisplayName("가맹점 저장 - 성공")
    void create_merchant() {
        Merchant actual = new Merchant("땅땅치킨");

        Long merchantId = merchantRepository.save(actual).getId();

        Merchant expect = em.find(Merchant.class, merchantId);

        assertThat(expect.getId()).isEqualTo(actual.getId());
        assertThat(expect.getName()).isEqualTo(actual.getName());
    }

    @Test
    @DisplayName("가맹점 수정 - 성공")
    void update_merchant() {
        Merchant actual = new Merchant("가가치킨");
        merchantRepository.save(actual);

        actual.modifyMerchant("네네치킨");

        Merchant expect = em.find(Merchant.class, actual.getId());

        assertThat(expect.getId()).isEqualTo(actual.getId());
        assertThat(expect.getName()).isEqualTo(actual.getName());
    }

    @Test
    @DisplayName("가맹점 존재 여부 - 성공")
    void exist_merchant() {
        Merchant actual = new Merchant("땅땅치킨");
        merchantRepository.save(actual);

        boolean expect = merchantRepository.existsMerchantByName(actual.getName());

        assertThat(expect).isTrue();
    }

}
