package store.cookshoong.www.cookshoongbackend.menu_order.service;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageService;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuHasMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.MenuHasOptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuGroupNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionGroupNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuGroupRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuStatusRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionGroupRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 메뉴 관리 서비스.
 *
 * @author papel (윤동현)
 * @contributor seungyeon (유승연)
 * @since 2023.07.11
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuStatusRepository menuStatusRepository;
    private final StoreRepository storeRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final FileStoreService fileStoreService;
    private final ObjectStorageService objectStorageService;

    /**
     * 메뉴 등록 서비스.
     *
     * @param storeId              매장 아이디
     * @param createMenuRequestDto 메뉴 등록 Dto
     */
    public void createMenu(Long storeId, CreateMenuRequestDto createMenuRequestDto, MultipartFile file)
        throws IOException {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        MenuStatus menuStatus = menuStatusRepository.findById(StoreStatus.StoreStatusCode.OPEN.name())
            .orElseThrow(MenuStatusNotFoundException::new);
        Image image = objectStorageService.storeFile(file, FileDomain.MENU_IMAGE.getVariable(), true);
        Menu menu =
            new Menu(
                menuStatus,
                store,
                createMenuRequestDto.getName(),
                createMenuRequestDto.getPrice(),
                createMenuRequestDto.getDescription(),
                image,
                createMenuRequestDto.getCookingTime(),
                createMenuRequestDto.getEarningRate());
        menuRepository.save(menu);
        updateMenuGroup(createMenuRequestDto.getMenuGroups(), menu.getId());
        updateOptionGroup(createMenuRequestDto.getOptionGroups(), menu.getId());
    }

    /**
     * 메뉴 리스트 조회 서비스.
     *
     * @param storeId 매장 아이디
     * @return 매장의 메뉴 리스트
     */
    public List<SelectMenuResponseDto> selectMenus(Long storeId) {
        List<SelectMenuResponseDto> responseDtos = menuRepository.lookupMenus(storeId);
        responseDtos.forEach(selectMenuResponseDto ->
            selectMenuResponseDto.setSavedName(objectStorageService.getFullPath(FileDomain.MENU_IMAGE.getVariable(),
                selectMenuResponseDto.getSavedName())));
        return responseDtos;
    }

    /**
     * 메뉴 조회 서비스.
     *
     * @param menuId 메뉴 아이디
     * @return 매장의 메뉴
     */
    public SelectMenuResponseDto selectMenu(Long menuId) {
        SelectMenuResponseDto responseDto = menuRepository.lookupMenu(menuId)
            .orElseThrow(MenuNotFoundException::new);

        responseDto.setSavedName(objectStorageService.getFullPath(FileDomain.MENU_IMAGE.getVariable(), responseDto.getSavedName()));
        return responseDto;
    }

    /**
     * 메뉴 삭제 서비스.
     *
     * @param storeId 매장 아이디
     * @param menuId  메뉴 아이디
     */
    public void deleteMenu(Long storeId, Long menuId) {
        menuRepository.deleteMenu(storeId, menuId);
    }

    private void updateMenuGroup(List<Long> menuGroups, Long menuId) {
        if (menuGroups.size() < 4) {
            for (Long menuGroupId : menuGroups) {
                MenuGroup menuGroup = menuGroupRepository.findById(menuGroupId)
                    .orElseThrow(MenuGroupNotFoundException::new);
                Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(MenuNotFoundException::new);
                menu.getMenuHasMenuGroups()
                    .add(new MenuHasMenuGroup(new MenuHasMenuGroup.Pk(menuId, menuGroupId), menu, menuGroup, 0));
            }
        }
    }

    private void updateOptionGroup(List<Long> optionGroups, Long menuId) {
        for (Long optionGroupId : optionGroups) {
            OptionGroup optionGroup = optionGroupRepository.findById(optionGroupId)
                .orElseThrow(OptionGroupNotFoundException::new);
            Menu menu = menuRepository.findById(menuId)
                .orElseThrow(MenuNotFoundException::new);
            menu.getMenuHasOptionGroups()
                .add(new MenuHasOptionGroup(new MenuHasOptionGroup.Pk(menuId, optionGroupId), menu, optionGroup, 0));
        }
    }
}
