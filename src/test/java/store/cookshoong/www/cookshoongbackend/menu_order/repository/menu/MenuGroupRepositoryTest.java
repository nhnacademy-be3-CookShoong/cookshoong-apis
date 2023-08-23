package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.common.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuGroup;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;


@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class })
class MenuGroupRepositoryTest {

    @Autowired
    MenuGroupRepository menuGroupRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    TestEntity testEntity;

    @Autowired
    TestPersistEntity testPersistEntity;

    static Store store;
    static MenuGroup menuGroup;

    @BeforeEach
    void setup() {
        store = testPersistEntity.getOpenStore();
        menuGroup = new MenuGroup(store, "테스트메뉴그룹", "메뉴그룹설명", 0);

        testEntityManager.persist(store);
        testEntityManager.persist(menuGroup);
    }

    @Test
    @DisplayName("메뉴 그룹 저장 - 성공")
    void saveMenuGroup() {
        Long menuGroupId = menuGroupRepository.save(menuGroup).getId();

        MenuGroup actual = menuGroupRepository.findById(menuGroupId).orElseThrow();
        assertThat(actual.getId()).isEqualTo(menuGroupId);
        assertThat(actual.getStore()).isEqualTo(menuGroup.getStore());
        assertThat(actual.getName()).isEqualTo(menuGroup.getName());
        assertThat(actual.getDescription()).isEqualTo(menuGroup.getDescription());
        assertThat(actual.getMenuGroupSequence()).isEqualTo(menuGroup.getMenuGroupSequence());
    }

}
