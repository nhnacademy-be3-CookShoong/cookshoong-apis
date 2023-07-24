package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuGroupResponseDto;

/**
 * 메뉴 그룹 커스텀 레포지토리 인터페이스.
 *
 * @author papel (윤동현)
 * @since 2023.07.17
 */
@NoRepositoryBean
public interface MenuGroupRepositoryCustom {

    /**
     * 매장 메뉴 그룹 조회.
     *
     * @param menuGroupId 메뉴 그룹 아이디
     * @return 매장의 메뉴 그룹
     */
    Optional<SelectMenuGroupResponseDto> lookupMenuGroup(Long menuGroupId);

    /**
     * 매장 메뉴 그룹 리스트 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장의 메뉴 그룹 리스트
     */
    List<SelectMenuGroupResponseDto> lookupMenuGroups(Long storeId);
}
