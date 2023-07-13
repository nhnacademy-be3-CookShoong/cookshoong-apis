package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Holiday;

/**
 * 휴업일 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {
    List<Holiday> findByStore_Id(Long storeId);
}
