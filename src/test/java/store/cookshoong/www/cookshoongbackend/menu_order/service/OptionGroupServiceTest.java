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
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionGroupRepository;

@ExtendWith(MockitoExtension.class)
class OptionGroupServiceTest {

    @Mock
    private OptionGroupRepository optionGroupRepository;
    @InjectMocks
    private OptionGroupService optionGroupService;

    @Test
    @DisplayName("옵션 그룹 목록 조회 - 성공")
    void selectOptionGroups() {
        Long storeId = 1L;
        List<SelectOptionGroupResponseDto> selectOptionGroupResponseDtoList = List.of(
            new SelectOptionGroupResponseDto(1L,storeId, "옵션그룹1", 0,1, false),
            new SelectOptionGroupResponseDto(2L,storeId, "옵션그룹2", 0,1, false),
            new SelectOptionGroupResponseDto(3L,storeId, "옵션그룹3", 0,1, false)
        );

        when(optionGroupRepository.lookupOptionGroups(storeId)).thenReturn(selectOptionGroupResponseDtoList);

        List<SelectOptionGroupResponseDto> resultList = optionGroupService.selectOptionGroups(storeId);

        assertThat(resultList.get(0).getId()).isEqualTo(selectOptionGroupResponseDtoList.get(0).getId());
        assertThat(resultList.get(1).getId()).isEqualTo(selectOptionGroupResponseDtoList.get(1).getId());
        assertThat(resultList.get(2).getId()).isEqualTo(selectOptionGroupResponseDtoList.get(2).getId());
    }

    @Test
    @DisplayName("옵션 그룹 단건 조회 - 성공")
    void selectOptionGroup() {
        Long optionGroupId = 1L;
        SelectOptionGroupResponseDto selectOptionGroupResponseDto =
            new SelectOptionGroupResponseDto(optionGroupId,1L, "옵션그룹1", 0,0, false);

        when(optionGroupRepository.lookupOptionGroup(optionGroupId)).thenReturn(Optional.of(selectOptionGroupResponseDto));

        SelectOptionGroupResponseDto result = optionGroupService.selectOptionGroup(optionGroupId);

        assertThat(result.getId()).isEqualTo(selectOptionGroupResponseDto.getId());
        assertThat(result.getName()).isEqualTo(selectOptionGroupResponseDto.getName());
        assertThat(result.getMinSelectCount()).isEqualTo(selectOptionGroupResponseDto.getMinSelectCount());
        assertThat(result.getMaxSelectCount()).isEqualTo(selectOptionGroupResponseDto.getMaxSelectCount());
        assertThat(result.getIsDeleted()).isEqualTo(selectOptionGroupResponseDto.getIsDeleted());
    }
}
