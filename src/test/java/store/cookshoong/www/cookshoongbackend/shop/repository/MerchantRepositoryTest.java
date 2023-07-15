package store.cookshoong.www.cookshoongbackend.shop.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllMerchantsForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;

/**
 * 가맹점 레포지토리 테스트코드 작성.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
@DataJpaTest
@Import(QueryDslConfig.class)
class MerchantRepositoryTest {
    @Autowired
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

    @Test
    @DisplayName("가맹점 리스트 조회(페이지) - 성공")
    void select_categories_page() {
        for (int i = 1; i < 10; i++) {
            Merchant merchant = new Merchant("카테고리" + i);
            merchantRepository.save(merchant);
        }
        Pageable pageable = PageRequest.of(2, 3);
        Page<SelectAllMerchantsForUserResponseDto> expects = merchantRepository.lookupMerchantPage(pageable);

        assertThat(expects.get().count()).isEqualTo(3);
        assertThat(expects.get().findFirst().get().getMerchantName()).isEqualTo("카테고리7");
    }

}
