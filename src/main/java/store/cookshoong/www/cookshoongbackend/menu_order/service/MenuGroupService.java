package store.cookshoong.www.cookshoongbackend.menu_order.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menugroup.MenuGroupRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menugroup.MenuHasMenuGroupRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 관리자가 가맹점의 메뉴 그룹을 관리.
 * 메뉴 그룹 추가, 수정, 삭제.
 *
 * @author papel
 * @since 2023.07.11
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MenuGroupService {
    private final MenuGroupRepository menuGroupRepository;
    private final MenuHasMenuGroupRepository menuHasMenuGroupRepository;
    private final StoreRepository storeRepository;

    /**
     * 사업자 : 메뉴 그룹 등록 서비스 구현.
     * 메뉴 그룹 등록, 처음 순서는 0으로 고정.
     *
     * @param storeId                   매장 아이디
     * @param createMenuGroupRequestDto 메뉴 그룹 등록을 위한 정보
     */
    public void createMenuGroup(Long storeId, CreateMenuGroupRequestDto createMenuGroupRequestDto) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        MenuGroup menuGroup =
            new MenuGroup(
                store,
                createMenuGroupRequestDto.getName(),
                createMenuGroupRequestDto.getDescription(),
                0);
        menuGroupRepository.save(menuGroup);
    }
}
