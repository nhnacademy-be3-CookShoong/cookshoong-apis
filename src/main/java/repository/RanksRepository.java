package repository;

import store.cookshoong.www.cookshoongbackend.accounts.entity.Ranks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RanksRepository extends JpaRepository<Ranks, String>, JpaSpecificationExecutor<Ranks> {

}