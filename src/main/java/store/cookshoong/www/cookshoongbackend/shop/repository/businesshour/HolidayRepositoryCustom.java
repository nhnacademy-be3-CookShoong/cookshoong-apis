package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectHolidayResponseDto;

/**
 * 휴업일 커스텀 레포지토리 인터페이텀.
 *
 * @author papel (윤동현)
 * @since 2023.07.06
 */
@NoRepositoryBean
public interface HolidayRepositoryCustom {
    /**
     * 매장 휴업일 리스트 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장의 휴업일 리스트
     */
    List<SelectHolidayResponseDto> lookupHolidays(Long storeId);
}
