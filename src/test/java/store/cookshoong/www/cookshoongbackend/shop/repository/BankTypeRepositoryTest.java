package store.cookshoong.www.cookshoongbackend.shop.repository;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.exception.banktype.BankTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.bank.BankTypeRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

/**
 * 은행 관리 레포지토리 테스트.
 *
 * @author seungyeon
 * @since 2023.07.09
 */
@DataJpaTest
@Import({QueryDslConfig.class, TestEntity.class})
class BankTypeRepositoryTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    TestEntity testEntity;

    @Autowired
    TestEntityManager em;

    @Autowired
    BankTypeRepository bankTypeRepository;

    @Test
    @DisplayName("은행 조회 - 성공")
    void select_bank() {
        BankType actual = testEntity.getBankTypeKb();
        String bankName = bankTypeRepository.save(actual).getDescription();

        BankType expect = bankTypeRepository.findByDescription(bankName).orElseThrow();

        assertThat(expect.getBankTypeCode()).isEqualTo(actual.getBankTypeCode());
        assertThat(expect.getDescription()).isEqualTo(actual.getDescription());
    }

    @Test
    @DisplayName("은행 추가 - 성공")
    void create_bank() {
        BankType actual = testEntity.getBankTypeKb();
        bankTypeRepository.save(actual);

        BankType expect = em.find(BankType.class, actual.getBankTypeCode());

        assertThat(expect.getBankTypeCode()).isEqualTo(actual.getBankTypeCode());
        assertThat(expect.getDescription()).isEqualTo(actual.getDescription());
    }

    @Test
    @DisplayName("은행 존재 여부 - 성공")
    void exist_by_bankName() {
        BankType actual = testEntity.getBankTypeKb();
        bankTypeRepository.save(actual);

        boolean expect = bankTypeRepository.existsByDescription(actual.getDescription());

        assertThat(expect).isTrue();
    }

    @Test
    @DisplayName("은행 리스트 조회(페이지) - 성공")
    void select_banks_page() {
        for (int i = 1; i < 10; i++) {
            BankType bankType = new BankType("Code" + i, "은행" + i);
            bankTypeRepository.save(bankType);
        }
        bankTypeRepository.flush();

        Pageable pageable = PageRequest.of(1, 4);
        Page<SelectAllBanksResponseDto> expects = bankTypeRepository.lookupBanksPage(pageable);

        assertThat(expects.get().count()).isEqualTo(4);
        assertThat(expects.get().findFirst().get().getDescription()).isEqualTo("은행5");
    }

    @Test
    @DisplayName("은행 조회 - 실패")
    void select_bank_fail() {
        assertThatThrownBy(() -> bankTypeRepository.findByDescription("nobanks")
            .orElseThrow(BankTypeNotFoundException::new))
            .isInstanceOf(BankTypeNotFoundException.class)
            .hasMessageContaining("해당 은행은 서비스 제공되지 않습니다.");
    }

}
