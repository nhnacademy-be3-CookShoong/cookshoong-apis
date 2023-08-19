package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class })
class OptionGroupRepositoryTest {

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
    @DisplayName("옵션 그룹 저장 - 성공")
    void saveOptionGroup() {
        Long optionGroupId = optionGroupRepository.save(optionGroup).getId();

        OptionGroup actual = optionGroupRepository.findById(optionGroupId).orElseThrow();
        assertThat(actual.getId()).isEqualTo(optionGroupId);
        assertThat(actual.getStore()).isEqualTo(optionGroup.getStore());
        assertThat(actual.getName()).isEqualTo(optionGroup.getName());
        assertThat(actual.getMinSelectCount()).isEqualTo(optionGroup.getMinSelectCount());
        assertThat(actual.getMaxSelectCount()).isEqualTo(optionGroup.getMaxSelectCount());
        assertThat(actual.getIsDeleted()).isEqualTo(optionGroup.getIsDeleted());
    }
}
