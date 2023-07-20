package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionResponseDto;

/**
 * 옵션 커스텀 레포지토리 인터페이스.
 *
 * @author papel
 * @since 2023.07.17
 */
@NoRepositoryBean
public interface OptionRepositoryCustom {

    /**
     * 메뉴 옵션 조회.
     *
     * @param optionId 옵션 아이디
     * @return 매장의 옵션
     */
    Optional<SelectOptionResponseDto> lookupOption(Long optionId);

    /**
     * 메뉴 옵션 리스트 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장의 옵션 리스트
     */
    List<SelectOptionResponseDto> lookupOptions(Long storeId);
}
