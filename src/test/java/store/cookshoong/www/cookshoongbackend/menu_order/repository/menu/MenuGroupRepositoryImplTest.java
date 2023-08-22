package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

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
import store.cookshoong.www.cookshoongbackend.common.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuGroupNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class})
class MenuGroupRepositoryImplTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

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
    @DisplayName("메뉴 그룹 단건 조회 - 성공")
    void lookupMenuGroup() {

        menuGroupRepository.save(menuGroup);

        SelectMenuGroupResponseDto actual = menuGroupRepository.lookupMenuGroup(menuGroup.getId()).orElseThrow(MenuGroupNotFoundException::new);
        assertThat(actual.getId()).isEqualTo(menuGroup.getId());
        assertThat(actual.getStoreId()).isEqualTo(menuGroup.getStore().getId());
        assertThat(actual.getName()).isEqualTo(menuGroup.getName());
        assertThat(actual.getDescription()).isEqualTo(menuGroup.getDescription());
        assertThat(actual.getMenuGroupSequence()).isEqualTo(menuGroup.getMenuGroupSequence());
    }

    @Test
    @DisplayName("메뉴 그룹 목록 조회 - 성공")
    void lookupMenuGroups() {
        for (int i = 1; i < 10; i++) {
            MenuGroup menuGroup = new MenuGroup(store, "테스트메뉴그룹", "메뉴 그룹 설명" , 0);
            menuGroupRepository.save(menuGroup);
        }

        List<SelectMenuGroupResponseDto> actuals = menuGroupRepository.lookupMenuGroups(store.getId());
        assertThat(actuals.size()).isEqualTo(10);
    }
}
