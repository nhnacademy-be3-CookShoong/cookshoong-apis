package repository;

import store.cookshoong.www.cookshoongbackend.order.OptionGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OptionGroupsRepository extends JpaRepository<OptionGroups, Integer>, JpaSpecificationExecutor<OptionGroups> {

}