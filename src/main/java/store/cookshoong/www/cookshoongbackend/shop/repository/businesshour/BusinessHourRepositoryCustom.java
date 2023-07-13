package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectBusinessHourResponseDto;

/**
 * 영업시간 커스텀 레포지토리 인터페이스.
 *
 * @author papel
 * @since 2023.07.10
 */
@NoRepositoryBean
public interface BusinessHourRepositoryCustom {

    /**
     * 영업시간 리스트 페이지별 조회.
     *
     * @param pageable 페이지 정보
     * @return 각 매장별 영업시간 리스트
     */
    Page<SelectBusinessHourResponseDto> lookupBusinessHourPage(Long storeId, Pageable pageable);
}
