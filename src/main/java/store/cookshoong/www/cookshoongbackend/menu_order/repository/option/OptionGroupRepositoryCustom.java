package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionGroupResponseDto;

/**
 * 옵션 그룹 커스텀 레포지토리 인터페이스.
 *
 * @author papel
 * @since 2023.07.17
 */
@NoRepositoryBean
public interface OptionGroupRepositoryCustom {

    /**
     * 매장 옵션 그룹 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장의 메뉴 리스트
     */
    List<SelectOptionGroupResponseDto> lookupOptionGroups(Long storeId);
}
