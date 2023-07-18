package store.cookshoong.www.cookshoongbackend.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu.exception.menu.MenuStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu.model.request.CreateMenuRequestDto;
import store.cookshoong.www.cookshoongbackend.menu.repository.menu.MenuRepository;
import store.cookshoong.www.cookshoongbackend.menu.repository.menu.MenuStatusRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.SelectStoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 관리자가 가맹점의 메뉴를 관리.
 * 메뉴 추가, 수정, 삭제.
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
     * 사업자 : 메뉴 등록 서비스 구현.
     * 메뉴 등록, 처음 상태는 판매중으로 고정.
     *
     * @param storeId              매장 아이디
     * @param createMenuRequestDto 메뉴 등록을 위한 정보
     */
    public void createMenu(Long storeId, CreateMenuRequestDto createMenuRequestDto) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(SelectStoreNotFoundException::new);
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
}