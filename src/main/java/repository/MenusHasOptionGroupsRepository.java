package repository;

import entity.MenusHasOptionGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenusHasOptionGroupsRepository extends JpaRepository<MenusHasOptionGroups, Integer>, JpaSpecificationExecutor<MenusHasOptionGroups> {

}