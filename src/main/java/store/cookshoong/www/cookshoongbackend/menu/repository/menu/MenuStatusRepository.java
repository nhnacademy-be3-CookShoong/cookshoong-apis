package store.cookshoong.www.cookshoongbackend.menu.repository.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.menu.entity.menu.MenuStatus;

/**
 * 메뉴 상태 레포지토리.
 *
 * @author papel
 * @since 2023.07.11
 */
public interface MenuStatusRepository extends JpaRepository<MenuStatus, String> {
}
