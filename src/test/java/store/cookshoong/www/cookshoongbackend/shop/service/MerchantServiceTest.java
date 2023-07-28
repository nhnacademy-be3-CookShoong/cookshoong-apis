package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllMerchantsResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;

/**
 * 관리자 : 가맹점 조회, 생성 관련 서비스 코드 테스트.
 * 사업자 : 가맹점 조회 코드 서비스 테스트.
 *
 * @author seungyeon
 * @since 2023.07.21
 */
@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {
    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private MerchantService merchantService;

    @Test
    @DisplayName("가맹점 조회 - 페이지별 정보 가져오기")
    void selectAllMerchants() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        List<SelectAllMerchantsResponseDto> selectAllMerchantsResponseDtos = List.of(
            new SelectAllMerchantsResponseDto(1L, "도미노 피자"),
            new SelectAllMerchantsResponseDto(2L, "또봉이 치킨"),
            new SelectAllMerchantsResponseDto(3L, "투썸플레이스"),
            new SelectAllMerchantsResponseDto(4L, "스타벅스"),
            new SelectAllMerchantsResponseDto(5L, "한솥도시락")
        );
        Page<SelectAllMerchantsResponseDto> extectedPage
            = new PageImpl<>(selectAllMerchantsResponseDtos, pageable, selectAllMerchantsResponseDtos.size());
        when(merchantRepository.lookupMerchantPage(pageable)).thenReturn(extectedPage);

        //when
        Page<SelectAllMerchantsResponseDto> resultPages = merchantService.selectAllMerchants(pageable);

        //then
        assertThat(resultPages.getContent()).isEqualTo(extectedPage.getContent());
        assertThat(resultPages.getTotalElements()).isEqualTo(extectedPage.getTotalElements());

        verify(merchantRepository, times(1)).lookupMerchantPage(any(Pageable.class));
    }

    @Test
    void selectAllMerchantsForUser() {
        // Given
        List<SelectAllMerchantsResponseDto> selectAllMerchantsResponseDtos = List.of(
            new SelectAllMerchantsResponseDto(1L, "도미노 피자"),
            new SelectAllMerchantsResponseDto(2L, "또봉이 치킨"),
            new SelectAllMerchantsResponseDto(3L, "스타벅스"),
            new SelectAllMerchantsResponseDto(4L, "투썸플레이스"),
            new SelectAllMerchantsResponseDto(5L, "한솥도시락")
        );
        when(merchantRepository.findAllByOrderByNameAsc()).thenReturn(selectAllMerchantsResponseDtos);

        // When
        List<SelectAllMerchantsResponseDto> result = merchantService.selectAllMerchantsForUser();

        // Then
        assertThat(result).isEqualTo(selectAllMerchantsResponseDtos);

        verify(merchantRepository, times(1)).findAllByOrderByNameAsc();
    }
}
