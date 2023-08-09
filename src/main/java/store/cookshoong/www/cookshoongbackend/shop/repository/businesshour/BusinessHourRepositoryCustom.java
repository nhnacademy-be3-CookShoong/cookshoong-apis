package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectBusinessHourResponseDto;

/**
 * 영업시간 커스텀 레포지토리 인터페이스.
 *
 * @author papel (윤동현)
 * @since 2023.07.10
 */
@NoRepositoryBean
public interface BusinessHourRepositoryCustom {
    /**
     * 매장 영업시간 리스트 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장의 영업시간 리스트
     */
    List<SelectBusinessHourResponseDto> lookupBusinessHours(Long storeId);
}
