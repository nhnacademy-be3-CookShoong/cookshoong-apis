package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.exception.merchant.DuplicatedMerchantException;
import store.cookshoong.www.cookshoongbackend.shop.exception.merchant.MerchantNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateMerchantRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateMerchantRequestDto;
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
    @DisplayName("가맹점 추가 - 성공")
    void createMerchant() {
        //given
        CreateMerchantRequestDto merchant = ReflectionUtils.newInstance(CreateMerchantRequestDto.class);
        ReflectionTestUtils.setField(merchant, "merchantName", "피자헛");

        when(merchantRepository.existsMerchantByName(merchant.getMerchantName())).thenReturn(false);

        //when
        merchantService.createMerchant(merchant);

        //then
        verify(merchantRepository, times(1)).existsMerchantByName(merchant.getMerchantName());
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    @Test
    @DisplayName("가맹점 추가 - 실패")
    void createMerchant_fail() {
        //given
        CreateMerchantRequestDto merchant = ReflectionUtils.newInstance(CreateMerchantRequestDto.class);
        ReflectionTestUtils.setField(merchant, "merchantName", "피자헛");

        when(merchantRepository.existsMerchantByName(merchant.getMerchantName())).thenReturn(true);

        assertThatThrownBy(() -> merchantService.createMerchant(merchant))
            .isInstanceOf(DuplicatedMerchantException.class)
            .hasMessageContaining("피자헛는 이미 등록된 가맹점입니다.");

        //then
        verify(merchantRepository, times(1)).existsMerchantByName(merchant.getMerchantName());
        verify(merchantRepository, times(0)).save(any(Merchant.class));
    }


    @Test
    @DisplayName("가맹점 수정 - 성공")
    void updateMerchant() {
        // given
        Long merchantId = 1L;
        UpdateMerchantRequestDto updatedMerchantDto = ReflectionUtils.newInstance(UpdateMerchantRequestDto.class);
        ReflectionTestUtils.setField(updatedMerchantDto, "merchantName", "도미노피자");

        Merchant existMerchant = new Merchant("이미 존재하는 피자집");
        ReflectionTestUtils.setField(existMerchant, "id", 1L);


        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(existMerchant));
        when(merchantRepository.existsMerchantByName(updatedMerchantDto.getMerchantName())).thenReturn(false);

        merchantService.updateMerchant(merchantId, updatedMerchantDto);

        verify(merchantRepository, times(1)).findById(merchantId);
        verify(merchantRepository, times(1)).existsMerchantByName(updatedMerchantDto.getMerchantName());

        assertThat(existMerchant.getName()).isEqualTo(updatedMerchantDto.getMerchantName());

    }

    @Test
    @DisplayName("가맹점 수정 - 실패")
    void updateMerchant_fail() {
        // given
        Long merchantId = 1L;
        UpdateMerchantRequestDto updatedMerchantDto = ReflectionUtils.newInstance(UpdateMerchantRequestDto.class);
        ReflectionTestUtils.setField(updatedMerchantDto, "merchantName", "도미노피자");

        Merchant existMerchant = new Merchant("이미 존재하는 피자집");
        ReflectionTestUtils.setField(existMerchant, "id", 1L);


        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(existMerchant));
        when(merchantRepository.existsMerchantByName(updatedMerchantDto.getMerchantName())).thenReturn(true);

        assertThatThrownBy(
            () -> merchantService.updateMerchant(merchantId, updatedMerchantDto))
            .isInstanceOf(DuplicatedMerchantException.class)
            .hasMessageContaining("도미노피자는 이미 등록된 가맹점입니다.");

        verify(merchantRepository, times(1)).findById(merchantId);
        verify(merchantRepository, times(1)).existsMerchantByName(updatedMerchantDto.getMerchantName());

        assertThat(existMerchant.getName()).isNotEqualTo(updatedMerchantDto.getMerchantName());

    }

    @Test
    @DisplayName("가맹점 삭제 - 성공")
    void removeMerchant() {
        Long merchantId = 1L;
        when(merchantRepository.existsById(merchantId)).thenReturn(true);

        merchantService.removeMerchant(merchantId);

        verify(merchantRepository, times(1)).existsById(merchantId);
        verify(merchantRepository, times(1)).deleteById(merchantId);
    }

    @Test
    @DisplayName("가맹점 삭제 - 존재하지 않아 실패")
    void removeMerchant_fail() {
        Long notExistId = 2L;
        when(merchantRepository.existsById(notExistId)).thenReturn(false);

        assertThrows(MerchantNotFoundException.class, () -> merchantService.removeMerchant(notExistId));

        verify(merchantRepository, times(1)).existsById(notExistId);
        verify(merchantRepository, times(0)).deleteById(notExistId);
    }

    @Test
    @DisplayName("가맹점리스트 조회 - 리스트로 가져오기")
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
