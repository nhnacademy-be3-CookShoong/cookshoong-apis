package store.cookshoong.www.cookshoongbackend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.store.entity.Holiday;

/**
 * 휴무일 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {
}
