package store.cookshoong.www.cookshoongbackend.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.store.model.response.HolidayListResponseDto;

/**
 * 휴업일 Custom 레포지토리.
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
    Page<HolidayListResponseDto> lookupHolidayPage(Long storeId, Pageable pageable);

}
