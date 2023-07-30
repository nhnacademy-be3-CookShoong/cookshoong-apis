package store.cookshoong.www.cookshoongbackend.menu_order.service;


import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuGroupNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuGroupRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 메뉴 그룹 관리 서비스.
 *
 * @author papel (윤동현)
 * @since 2023.07.11
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MenuGroupService {
    private final MenuGroupRepository menuGroupRepository;
    private final StoreRepository storeRepository;

    /**
     * 메뉴 그룹 등록 및 수정 서비스.
     *
     * @param storeId             매장 아이디
     * @param createMenuGroupRequestDto 메뉴 그룹 등록 Dto
     */
    public void updateMenuGroup(Long storeId, CreateMenuGroupRequestDto createMenuGroupRequestDto) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        if (Objects.isNull(createMenuGroupRequestDto.getTargetMenuGroupId())) {
            MenuGroup menuGroup =
                new MenuGroup(
                    store,
                    createMenuGroupRequestDto.getName(),
                    createMenuGroupRequestDto.getDescription(),
                    0);
            menuGroupRepository.save(menuGroup);
        } else {
            MenuGroup menuGroup = menuGroupRepository.findById(createMenuGroupRequestDto.getTargetMenuGroupId())
                .orElseThrow(MenuGroupNotFoundException::new);
            menuGroup.modifyMenuGroup(createMenuGroupRequestDto);
        }
    }

    /**
     * 메뉴 그룹 리스트 조회 서비스.
     *
     * @param storeId 매장 아이디
     * @return 매장의 메뉴 그룹 리스트
     */
    public List<SelectMenuGroupResponseDto> selectMenuGroups(Long storeId) {
        return menuGroupRepository.lookupMenuGroups(storeId);
    }

    /**
     * 메뉴 그룹 조회 서비스.
     *
     * @param menuGroupId 메뉴 그룹 아이디
     * @return 매장의 메뉴 그룹
     */
    public SelectMenuGroupResponseDto selectMenuGroup(Long menuGroupId) {
        return menuGroupRepository.lookupMenuGroup(menuGroupId)
            .orElseThrow(MenuGroupNotFoundException::new);
    }

    /**
     * 메뉴 그룹 삭제 서비스.
     *
     * @param storeId     매장 아이디
     * @param menuGroupId 메뉴 그룹 아이디
     */
    public void deleteMenuGroup(Long storeId, Long menuGroupId) {
        menuGroupRepository.deleteById(menuGroupId);
    }
}
