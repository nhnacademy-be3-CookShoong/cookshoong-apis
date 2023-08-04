package store.cookshoong.www.cookshoongbackend.menu_order.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuGroupRepository;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;
    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴 그룹 목록 조회 - 성공")
    void selectMenuGroups() {
        Long storeId = 1L;
        List<SelectMenuGroupResponseDto> selectMenuGroupResponseDtoList = List.of(
            new SelectMenuGroupResponseDto(1L,storeId, "메뉴그룹1", "그룹설명1",0),
            new SelectMenuGroupResponseDto(2L,storeId, "메뉴그룹2", "그룹설명2",0),
            new SelectMenuGroupResponseDto(3L,storeId, "메뉴그룹3", "그룹설명3",0)
        );

        when(menuGroupRepository.lookupMenuGroups(storeId)).thenReturn(selectMenuGroupResponseDtoList);

        List<SelectMenuGroupResponseDto> resultList = menuGroupService.selectMenuGroups(storeId);

        assertThat(resultList.get(0).getId()).isEqualTo(selectMenuGroupResponseDtoList.get(0).getId());
        assertThat(resultList.get(1).getId()).isEqualTo(selectMenuGroupResponseDtoList.get(1).getId());
        assertThat(resultList.get(2).getId()).isEqualTo(selectMenuGroupResponseDtoList.get(2).getId());
    }

    @Test
    @DisplayName("메뉴 그룹 단건 조회 - 성공")
    void selectMenuGroup() {
        Long menuGroupId = 1L;
        SelectMenuGroupResponseDto selectMenuGroupResponseDto =
            new SelectMenuGroupResponseDto(menuGroupId,1L, "메뉴그룹1", "그룹설명1",0);

        when(menuGroupRepository.lookupMenuGroup(menuGroupId)).thenReturn(Optional.of(selectMenuGroupResponseDto));

        SelectMenuGroupResponseDto result = menuGroupService.selectMenuGroup(menuGroupId);

        assertThat(result.getId()).isEqualTo(selectMenuGroupResponseDto.getId());
        assertThat(result.getName()).isEqualTo(selectMenuGroupResponseDto.getName());
        assertThat(result.getDescription()).isEqualTo(selectMenuGroupResponseDto.getDescription());
        assertThat(result.getStoreId()).isEqualTo(selectMenuGroupResponseDto.getStoreId());
        assertThat(result.getMenuGroupSequence()).isEqualTo(selectMenuGroupResponseDto.getMenuGroupSequence());
    }
}
