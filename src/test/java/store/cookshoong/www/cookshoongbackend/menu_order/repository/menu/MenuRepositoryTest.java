package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
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
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class})
class MenuRepositoryTest {
    @Autowired
    MenuRepository menuRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    TestEntity testEntity;

    @Autowired
    TestPersistEntity testPersistEntity;

    static Store store;
    static Image image;
    static Menu menu;
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
    @DisplayName("메뉴 저장 - 성공")
    void saveMenu() {
        Long menuId = menuRepository.save(menu).getId();

        Menu actual = menuRepository.findById(menuId).orElseThrow();
        assertThat(actual.getId()).isEqualTo(menuId);
        assertThat(actual.getMenuStatus()).isEqualTo(menu.getMenuStatus());
        assertThat(actual.getStore()).isEqualTo(menu.getStore());
        assertThat(actual.getName()).isEqualTo(menu.getName());
        assertThat(actual.getPrice()).isEqualTo(menu.getPrice());
        assertThat(actual.getDescription()).isEqualTo(menu.getDescription());
        assertThat(actual.getImage()).isEqualTo(menu.getImage());
        assertThat(actual.getCookingTime()).isEqualTo(menu.getCookingTime());
        assertThat(actual.getEarningRate()).isEqualTo(menu.getEarningRate());
    }



}
