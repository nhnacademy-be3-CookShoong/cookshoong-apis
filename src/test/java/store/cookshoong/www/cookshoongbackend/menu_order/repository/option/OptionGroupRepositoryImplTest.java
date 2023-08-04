package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionGroupNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * {설명을 작성해주세요}
 *
 * @author papel
 * @since 2023.08.03
 */
@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class})
class OptionGroupRepositoryImplTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    OptionGroupRepository optionGroupRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    TestEntity testEntity;

    @Autowired
    TestPersistEntity testPersistEntity;

    static Store store;
    static OptionGroup optionGroup;

    @BeforeEach
    void setup() {
        store = testPersistEntity.getOpenStore();
        optionGroup = new OptionGroup(store, "테스트옵션그룹", 0, 1, false);

        testEntityManager.persist(store);
        testEntityManager.persist(optionGroup);
    }

    @Test
    @DisplayName("옵션 그룹 단건 조회 - 성공")
    void lookupOptionGroup() {

        optionGroupRepository.save(optionGroup);

        SelectOptionGroupResponseDto actual = optionGroupRepository.lookupOptionGroup(optionGroup.getId()).orElseThrow(OptionGroupNotFoundException::new);
        assertThat(actual.getId()).isEqualTo(optionGroup.getId());
        assertThat(actual.getStoreId()).isEqualTo(optionGroup.getStore().getId());
        assertThat(actual.getName()).isEqualTo(optionGroup.getName());
        assertThat(actual.getMinSelectCount()).isEqualTo(optionGroup.getMinSelectCount());
        assertThat(actual.getMaxSelectCount()).isEqualTo(optionGroup.getMaxSelectCount());
        assertThat(actual.getIsDeleted()).isEqualTo(optionGroup.getIsDeleted());
    }

    @Test
    @DisplayName("옵션 그룹 목록 조회 - 성공")
    void lookupOptionGroups() {
        for (int i = 1; i < 10; i++) {
            OptionGroup optionGroup = new OptionGroup(store, "테스트메뉴그룹", 0 , 1, false);
            optionGroupRepository.save(optionGroup);
        }

        List<SelectOptionGroupResponseDto> actuals = optionGroupRepository.lookupOptionGroups(store.getId());
        assertThat(actuals.size()).isEqualTo(10);
    }
}
