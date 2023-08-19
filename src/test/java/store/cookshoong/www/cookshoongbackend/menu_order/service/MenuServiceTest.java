package store.cookshoong.www.cookshoongbackend.menu_order.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.cookshoong.www.cookshoongbackend.common.property.ObjectStorageProperties;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtilResolver;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtils;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageAuth;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageService;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuRepository;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private FileUtilResolver fileUtilResolver;
    @Mock
    private FileUtils fileUtils;
    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 목록 조회 - 성공")
    void selectMenus() {
        Long storeId = 1L;
        MenuStatus menuStatus = new MenuStatus("OPEN", "판매중");
        List<SelectMenuResponseDto> selectMenuResponseDtoList = List.of(
            new SelectMenuResponseDto(1L, "OPEN", storeId, "메뉴1", 5000,
                "메뉴설명",UUID.randomUUID()+".jpg",40, new BigDecimal(1.0),
                LocationType.OBJECT_S.getVariable(), FileDomain.MENU_IMAGE.getVariable()),
            new SelectMenuResponseDto(2L, "OPEN", storeId, "메뉴2", 5000,
                "메뉴설명",UUID.randomUUID()+".jpg",40, new BigDecimal(1.0),
                LocationType.OBJECT_S.getVariable(), FileDomain.MENU_IMAGE.getVariable()),
            new SelectMenuResponseDto(3L, "OPEN", storeId, "메뉴3", 5000,
                "메뉴설명", UUID.randomUUID()+".jpg",40, new BigDecimal(1.0),
                LocationType.OBJECT_S.getVariable(), FileDomain.MENU_IMAGE.getVariable())
        );

        when(menuRepository.lookupMenus(storeId)).thenReturn(selectMenuResponseDtoList);
        for (SelectMenuResponseDto dto : selectMenuResponseDtoList){
            when(fileUtilResolver.getFileService(dto.getLocationType())).thenReturn(fileUtils);
        }
        List<SelectMenuResponseDto> resultList = menuService.selectMenus(storeId);

        assertThat(resultList.get(0).getId()).isEqualTo(selectMenuResponseDtoList.get(0).getId());
        assertThat(resultList.get(1).getId()).isEqualTo(selectMenuResponseDtoList.get(1).getId());
        assertThat(resultList.get(2).getId()).isEqualTo(selectMenuResponseDtoList.get(2).getId());
    }

    @Test
    @DisplayName("메뉴 단건 조회 - 성공")
    void selectMenu() {
        Long menuId = 1L;
        SelectMenuResponseDto selectMenuResponseDto =
            new SelectMenuResponseDto(menuId, "OPEN", 1L, "메뉴1", 5000,
                "메뉴설명","메뉴사진",40, new BigDecimal(1.0),
                LocationType.OBJECT_S.getVariable(), FileDomain.MENU_IMAGE.getVariable());

        when(menuRepository.lookupMenu(menuId)).thenReturn(Optional.of(selectMenuResponseDto));
        when(fileUtilResolver.getFileService(selectMenuResponseDto.getLocationType())).thenReturn(fileUtils);

        SelectMenuResponseDto result = menuService.selectMenu(menuId);

        assertThat(result.getId()).isEqualTo(selectMenuResponseDto.getId());
        assertThat(result.getMenuStatus()).isEqualTo(selectMenuResponseDto.getMenuStatus());
        assertThat(result.getStoreId()).isEqualTo(selectMenuResponseDto.getStoreId());
        assertThat(result.getName()).isEqualTo(selectMenuResponseDto.getName());
        assertThat(result.getPrice()).isEqualTo(selectMenuResponseDto.getPrice());
        assertThat(result.getDescription()).isEqualTo(selectMenuResponseDto.getDescription());
        assertThat(result.getSavedName()).isEqualTo(selectMenuResponseDto.getSavedName());
        assertThat(result.getCookingTime()).isEqualTo(selectMenuResponseDto.getCookingTime());
        assertThat(result.getEarningRate()).isEqualTo(selectMenuResponseDto.getEarningRate());
    }
}
