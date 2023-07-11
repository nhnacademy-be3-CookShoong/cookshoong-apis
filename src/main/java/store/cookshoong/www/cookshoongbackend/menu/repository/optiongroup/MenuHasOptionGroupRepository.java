package store.cookshoong.www.cookshoongbackend.menu.repository.optiongroup;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.menu.entity.optiongroup.MenuHasOptionGroup;

/**
 * 메뉴-옵션그룹 레포지토리.
 *
 * @author papel
 * @since 2023.07.11
 */
public interface MenuHasOptionGroupRepository extends JpaRepository<MenuHasOptionGroup, MenuHasOptionGroup.Pk> {
}
