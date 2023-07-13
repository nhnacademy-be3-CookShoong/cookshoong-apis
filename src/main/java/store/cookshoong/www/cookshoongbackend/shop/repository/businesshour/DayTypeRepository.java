package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.DayType;

/**
 * 요일 타입 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface DayTypeRepository extends JpaRepository<DayType, String> {

    /**
     * 요일 이름으로 요일 객체 반환.
     *
     * @param description 요일 이름
     * @return 요일 객체
     */
    Optional<DayType> findByDescription(String description);

}
