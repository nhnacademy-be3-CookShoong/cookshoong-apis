package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionGroupResponseDto;

/**
 * 옵션 그룹 커스텀 레포지토리 인터페이스.
 *
 * @author papel (윤동현)
 * @since 2023.07.17
 */
@NoRepositoryBean
public interface OptionGroupRepositoryCustom {

    /**
     * 매장 옵션 그룹 조회.
     *
     * @param optionGroupId 옵션 그룹 아이디
     * @return 매장의 옵션 그룹
     */
    Optional<SelectOptionGroupResponseDto> lookupOptionGroup(Long optionGroupId);

    /**
     * 매장 옵션 그룹 리스트 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장의 옵션 그룹 리스트
     */
    List<SelectOptionGroupResponseDto> lookupOptionGroups(Long storeId);
}
