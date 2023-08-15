package store.cookshoong.www.cookshoongbackend.menu_order.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.file.ImageNotFoundException;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtilResolver;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtils;
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
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuHasMenuGroupRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuHasOptionGroupRepository;
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
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final MenuStatusRepository menuStatusRepository;
    private final ImageRepository imageRepository;
    private final MenuHasMenuGroupRepository menuHasMenuGroupRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuHasOptionGroupRepository menuHasOptionGroupRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final FileUtilResolver fileUtilResolver;
    private final EntityManager entityManager;

    private Image getImage(String storedAt, MultipartFile file, FileDomain fileDomain) throws IOException {
        if (Objects.nonNull(file)) {
            FileUtils fileUtils = fileUtilResolver.getFileService(storedAt);
            return fileUtils.storeFile(file, fileDomain.getVariable(), true);
        }
        return null;
    }

    public void createMenu(Long storeId, CreateMenuRequestDto createMenuRequestDto,
                           String storedAt, MultipartFile file) throws IOException {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        MenuStatus menuStatus = menuStatusRepository.findById(StoreStatus.StoreStatusCode.OPEN.name())
            .orElseThrow(MenuStatusNotFoundException::new);

        Menu menu = new Menu(
            menuStatus,
            store,
            createMenuRequestDto.getName(),
            createMenuRequestDto.getPrice(),
            createMenuRequestDto.getDescription(),
            getImage(storedAt, file, FileDomain.MENU_IMAGE),
            createMenuRequestDto.getCookingTime(),
            createMenuRequestDto.getEarningRate());
        menuRepository.save(menu);
        addMenuGroup(createMenuRequestDto.getMenuGroups(), menu.getId());
        addOptionGroup(createMenuRequestDto.getOptionGroups(), menu.getId());
    }

    /**
     * 메뉴 등록 및 수정 서비스.
     *
     * @param storeId              매장 아이디
     * @param createMenuRequestDto 메뉴 등록 Dto
     */
    public void updateMenu(CreateMenuRequestDto createMenuRequestDto,
                           String storedAt, MultipartFile file)
        throws IOException {
        Menu menu = menuRepository.findById(createMenuRequestDto.getTargetMenuId())
            .orElseThrow(MenuNotFoundException::new);

        Image newImage = getImage(storedAt, file, FileDomain.MENU_IMAGE);
        if (Objects.nonNull(menu.getImage())) {
            Image oldImage = imageRepository.findById(menu.getImage().getId()).orElseThrow(ImageNotFoundException::new);
            FileUtils fileUtils = fileUtilResolver.getFileService(oldImage.getLocationType());

            if (Objects.nonNull(newImage)) {
                fileUtils.deleteFile(oldImage);
                menu.modifyImage(newImage);
            }
        } else {
            menu.modifyImage(newImage);
        }
        menu.modifyMenu(createMenuRequestDto);
        clearMenuGroup(menu.getId());
        clearOptionGroup(menu.getId());
        entityManager.flush();
        addMenuGroup(createMenuRequestDto.getMenuGroups(), menu.getId());
        addOptionGroup(createMenuRequestDto.getOptionGroups(), menu.getId());
    }

    /**
     * 메뉴 리스트 조회 서비스.
     *
     * @param storeId 매장 아이디
     * @return 매장의 메뉴 리스트
     */
    public List<SelectMenuResponseDto> selectMenus(Long storeId) {
        List<SelectMenuResponseDto> responseDtos = menuRepository.lookupMenus(storeId);
        for (SelectMenuResponseDto dto : responseDtos) {
            if (Objects.nonNull(dto.getSavedName())) {
                FileUtils fileUtils = fileUtilResolver.getFileService(dto.getLocationType());
                dto.setSavedName(fileUtils.getFullPath(dto.getDomainName(), dto.getSavedName()));
            }
        }
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
        if (Objects.nonNull(responseDto.getSavedName())) {
            FileUtils fileUtils = fileUtilResolver.getFileService(responseDto.getLocationType());
            responseDto.setSavedName(fileUtils.getFullPath(responseDto.getDomainName(), responseDto.getSavedName()));
        }
        return responseDto;
    }

    /**
     * 메뉴 삭제 서비스.
     *
     * @param storeId 매장 아이디
     * @param menuId  메뉴 아이디
     */
    public void deleteMenu(Long storeId, Long menuId) throws IOException {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(MenuNotFoundException::new);
        MenuStatus menuStatus = menuStatusRepository.findById("OUTED")
            .orElseThrow(MenuStatusNotFoundException::new);
        menu.modifyMenuStatus(menuStatus);
        if (Objects.nonNull(menu.getImage())) {
            FileUtils fileUtils = fileUtilResolver.getFileService(menu.getImage().getLocationType());
            fileUtils.deleteFile(menu.getImage());
        }
    }

    /**
     * 메뉴 - 메뉴 그룹 관계 삭제 서비스.
     *
     * @param menuId 메뉴 아이디
     */
    private void clearMenuGroup(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(MenuNotFoundException::new);
        List<MenuHasMenuGroup> menuHasMenuGroups = new ArrayList<>(menu.getMenuHasMenuGroups());
        menuHasMenuGroupRepository.deleteAll(menuHasMenuGroups);
        menu.getMenuHasMenuGroups().clear();
    }

    /**
     * 메뉴 - 옵션 그룹 관계 삭제 서비스.
     *
     * @param menuId 메뉴 아이디
     */
    private void clearOptionGroup(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(MenuNotFoundException::new);
        List<MenuHasOptionGroup> menuHasOptionGroups = new ArrayList<>(menu.getMenuHasOptionGroups());
        menuHasOptionGroupRepository.deleteAll(menuHasOptionGroups);
        menu.getMenuHasOptionGroups().clear();
    }

    /**
     * 메뉴 - 메뉴 그룹 관계 업데이트 서비스.
     *
     * @param menuGroups 메뉴 그룹 리스트
     * @param menuId     메뉴 아이디
     */
    private void addMenuGroup(List<Long> menuGroups, Long menuId) {
        if (Objects.nonNull(menuGroups)) {
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

    /**
     * 메뉴 - 옵션 그룹 관계 업데이트 서비스.
     *
     * @param optionGroups 옵션 그룹 리스트
     * @param menuId       메뉴 아이디
     */
    private void addOptionGroup(List<Long> optionGroups, Long menuId) {
        if (Objects.nonNull(optionGroups)) {
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
}
