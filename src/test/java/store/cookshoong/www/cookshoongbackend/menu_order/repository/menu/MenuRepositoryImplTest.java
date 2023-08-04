package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;
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
class MenuRepositoryImplTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    TestEntity testEntity;

    @Autowired
    TestPersistEntity testPersistEntity;

    static Store store;
    static Menu menu;
    static Image image;
    static MenuStatus menuStatus;

    @BeforeEach
    void setup() {
        store = testPersistEntity.getOpenStore();
        image = testEntity.getImage("메뉴사진", true);
        menuStatus = testEntity.getMenuStatus("OPEN", "판매중");
        menu = new Menu(menuStatus, store, "테스트메뉴", 5000, "메뉴설명", image, 40, new BigDecimal("1.2"));

        testEntityManager.persist(store);
        testEntityManager.persist(image);
        testEntityManager.persist(menuStatus);
        testEntityManager.persist(menu);
    }

    @Test
    @DisplayName("메뉴 단건 조회 - 성공")
    void lookupMenu() {

        menuRepository.save(menu);

        SelectMenuResponseDto actual = menuRepository.lookupMenu(menu.getId()).orElseThrow(MenuNotFoundException::new);
        assertThat(actual.getId()).isEqualTo(menu.getId());
        assertThat(actual.getMenuStatus()).isEqualTo(menu.getMenuStatusCode().getMenuStatusCode());
        assertThat(actual.getStoreId()).isEqualTo(menu.getStore().getId());
        assertThat(actual.getName()).isEqualTo(menu.getName());
        assertThat(actual.getPrice()).isEqualTo(menu.getPrice());
        assertThat(actual.getDescription()).isEqualTo(menu.getDescription());
        assertThat(actual.getSavedName()).isEqualTo(menu.getImage().getSavedName());
        assertThat(actual.getCookingTime()).isEqualTo(menu.getCookingTime());
        assertThat(actual.getEarningRate()).isEqualTo(menu.getEarningRate());
    }

    @Test
    @DisplayName("메뉴 목록 조회 - 성공")
    void lookupMenus() {
        for (int i = 1; i < 10; i++) {
            Menu menu = new Menu(menuStatus, store, "테스트메뉴", 5000, "메뉴설명", image, 40, new BigDecimal("1.2"));
            menuRepository.save(menu);
        }

        List<SelectMenuResponseDto> actuals = menuRepository.lookupMenus(store.getId());
        assertThat(actuals.size()).isEqualTo(10);
    }
}
