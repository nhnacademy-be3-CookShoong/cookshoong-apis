package store.cookshoong.www.cookshoongbackend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreBusinessHour;

/**
 * 영업시간 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface StoreBusinessHourRepository extends JpaRepository<StoreBusinessHour, Long> {

}
