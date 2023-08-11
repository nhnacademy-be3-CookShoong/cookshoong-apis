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
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionRepository;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @Mock
    private OptionRepository optionRepository;
    @InjectMocks
    private OptionService optionService;

    @Test
    @DisplayName("옵션 목록 조회 - 성공")
    void selectOptions() {
        Long optionGroupId = 1L;
        List<SelectOptionResponseDto> selectOptionResponseDtoList = List.of(
            new SelectOptionResponseDto(1L, optionGroupId, "옵션1", 0, false, 0),
            new SelectOptionResponseDto(2L, optionGroupId, "옵션2", 0, false, 1),
            new SelectOptionResponseDto(3L, optionGroupId, "옵션3", 0, false, 2)
        );

        when(optionRepository.lookupOptions(optionGroupId)).thenReturn(selectOptionResponseDtoList);

        List<SelectOptionResponseDto> resultList = optionService.selectOptions(optionGroupId);

        assertThat(resultList.get(0).getId()).isEqualTo(selectOptionResponseDtoList.get(0).getId());
        assertThat(resultList.get(1).getId()).isEqualTo(selectOptionResponseDtoList.get(1).getId());
        assertThat(resultList.get(2).getId()).isEqualTo(selectOptionResponseDtoList.get(2).getId());
    }

    @Test
    @DisplayName("옵션 단건 조회 - 성공")
    void selectOption() {
        Long OptionId = 1L;
        SelectOptionResponseDto selectOptionResponseDto =
            new SelectOptionResponseDto(OptionId,1L, "옵션1", 0,false, 0);

        when(optionRepository.lookupOption(OptionId)).thenReturn(Optional.of(selectOptionResponseDto));

        SelectOptionResponseDto result = optionService.selectOption(OptionId);

        assertThat(result.getId()).isEqualTo(selectOptionResponseDto.getId());
        assertThat(result.getOptionGroupId()).isEqualTo(selectOptionResponseDto.getOptionGroupId());
        assertThat(result.getName()).isEqualTo(selectOptionResponseDto.getName());
        assertThat(result.getPrice()).isEqualTo(selectOptionResponseDto.getPrice());
        assertThat(result.getIsDeleted()).isEqualTo(selectOptionResponseDto.getIsDeleted());
        assertThat(result.getOptionSequence()).isEqualTo(selectOptionResponseDto.getOptionSequence());
    }
}
