package store.cookshoong.www.cookshoongbackend.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.store.model.response.HolidayResponseDto;

/**
 * 영업시간 Custom 레포지토리.
 *
 * @author papel
 * @since 2023.07.06
 */
@NoRepositoryBean
public interface HolidayRepositoryCustom {
    /**
     * 가맹점 리스트 페이지별 조회.
     *
     * @param pageable 페이지 정보
     * @return 각 페이지별 가맹점 리스트
     */
    Page<HolidayResponseDto> lookupHolidayPage(Long storeId, Pageable pageable);

}
