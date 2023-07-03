package repository;

import entity.MenuGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenuGroupsRepository extends JpaRepository<MenuGroups, Long>, JpaSpecificationExecutor<MenuGroups> {

}