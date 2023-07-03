package repository;

import entity.MenuHasMenuGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenuHasMenuGroupsRepository extends JpaRepository<MenuHasMenuGroups, Long>, JpaSpecificationExecutor<MenuHasMenuGroups> {

}