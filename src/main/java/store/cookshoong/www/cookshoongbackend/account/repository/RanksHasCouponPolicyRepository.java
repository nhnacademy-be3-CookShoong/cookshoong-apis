package store.cookshoong.www.cookshoongbackend.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.account.entity.RanksHasCouponPolicy;

public interface RanksHasCouponPolicyRepository extends JpaRepository<RanksHasCouponPolicy, RanksHasCouponPolicy.Pk> {
}
