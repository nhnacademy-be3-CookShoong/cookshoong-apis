package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectHolidayResponseDto;

/**
 * 휴업일 커스텀 레포지토리 인터페이텀.
 *
 * @author papel
 * @since 2023.07.06
 */
@NoRepositoryBean
public interface HolidayRepositoryCustom {

    /**
     * 휴업일 리스트 페이지별 조회.
     *
     * @param pageable 페이지 정보
     * @return 각 매장별 휴업일 리스트
     */
    Page<SelectHolidayResponseDto> lookupHolidayPage(Long storeId, Pageable pageable);
}
