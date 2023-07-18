package store.cookshoong.www.cookshoongbackend.menu_order.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuStatusRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 메뉴 관리 서비스.
 *
 * @author papel
 * @since 2023.07.11
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuStatusRepository menuStatusRepository;
    private final StoreRepository storeRepository;

    /**
     * 메뉴 등록 서비스.
     *
     * @param storeId              매장 아이디
     * @param createMenuRequestDto 메뉴 등록 Dto
     */
    public void createMenu(Long storeId, CreateMenuRequestDto createMenuRequestDto) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        MenuStatus menuStatus = menuStatusRepository.findById("OPEN").orElseThrow(MenuStatusNotFoundException::new);
        Menu menu =
            new Menu(
                menuStatus,
                store,
                createMenuRequestDto.getName(),
                createMenuRequestDto.getPrice(),
                createMenuRequestDto.getDescription(),
                createMenuRequestDto.getImage(),
                createMenuRequestDto.getCookingTime(),
                createMenuRequestDto.getEarningRate());
        menuRepository.save(menu);
    }

    /**
     * 메뉴 리스트 조회 서비스.
     *
     * @param storeId 매장 아이디
     * @return 매장의 메뉴 리스트
     */
    public List<SelectMenuResponseDto> selectMenus(Long storeId) {
        return menuRepository.lookupMenus(storeId);
    }

    /**
     * 메뉴 조회 서비스.
     *
     * @param menuId 메뉴 아이디
     * @return 매장의 메뉴
     */
    public SelectMenuResponseDto selectMenu(Long menuId) {
        return menuRepository.lookupMenu(menuId)
            .orElseThrow(MenuNotFoundException::new);
    }
}
