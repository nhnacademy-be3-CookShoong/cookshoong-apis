package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.common.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class })
class OptionRepositoryImplTest {
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
    @DisplayName("옵션 단건 조회 - 성공")
    void lookupOption() {

        optionRepository.save(option);

        SelectOptionResponseDto actual = optionRepository.lookupOption(option.getId()).orElseThrow(OptionNotFoundException::new);
        assertThat(actual.getId()).isEqualTo(option.getId());
        assertThat(actual.getOptionGroupId()).isEqualTo(option.getOptionGroup().getId());
        assertThat(actual.getName()).isEqualTo(option.getName());
        assertThat(actual.getPrice()).isEqualTo(option.getPrice());
        assertThat(actual.getOptionSequence()).isEqualTo(option.getOptionSequence());
        assertThat(actual.getIsDeleted()).isEqualTo(option.getIsDeleted());
    }

    @Test
    @DisplayName("옵션 목록 조회 - 성공")
    void lookupOptions() {
        for (int i = 1; i < 10; i++) {
            Option option = new Option( optionGroup, "테스트옵션", 500, 0, false);
            optionRepository.save(option);
        }

        List<SelectOptionResponseDto> actual = optionRepository.lookupOptions(store.getId());
        assertThat(actual).hasSize(10);
    }
}
