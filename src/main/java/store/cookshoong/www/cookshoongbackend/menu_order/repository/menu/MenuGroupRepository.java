package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuGroup;

/**
 * 메뉴 그룹 레포지토리.
 *
 * @author papel
 * @since 2023.07.11
 */
public interface MenuGroupRepository extends JpaRepository<MenuGroup, Long> {
}
