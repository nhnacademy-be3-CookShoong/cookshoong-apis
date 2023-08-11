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
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class })
class OptionRepositoryTest {
    @Autowired
    OptionRepository optionRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    TestEntity testEntity;

    @Autowired
    TestPersistEntity testPersistEntity;

    static Store store;
    static OptionGroup optionGroup;
    static Option option;

    @BeforeEach
    void setup() {
        store = testPersistEntity.getOpenStore();
        optionGroup = new OptionGroup(store, "테스트옵션그룹", 0, 1, false);
        option = new Option(optionGroup, "테스트옵션", 500, 0, false);

        testEntityManager.persist(store);
        testEntityManager.persist(optionGroup);
        testEntityManager.persist(option);
    }

    @Test
    @DisplayName("옵션 저장 - 성공")
    void saveOption() {
        Long optionId = optionRepository.save(option).getId();

        Option actual = optionRepository.findById(optionId).orElseThrow();
        assertThat(actual.getId()).isEqualTo(optionId);
        assertThat(actual.getOptionGroup()).isEqualTo(option.getOptionGroup());
        assertThat(actual.getName()).isEqualTo(option.getName());
        assertThat(actual.getPrice()).isEqualTo(option.getPrice());
        assertThat(actual.getOptionSequence()).isEqualTo(option.getOptionSequence());
        assertThat(actual.getIsDeleted()).isEqualTo(option.getIsDeleted());
    }

}
