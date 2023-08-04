package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.exception.banktype.DuplicatedBankException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateBankRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.bank.BankTypeRepository;

/**
 * 관리자 : 은행 조회, 생성 관련 서비스 코드 테스트.
 * 사업자 : 은행 조회 코드 서비스 테스트.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.21
 */
@ExtendWith(MockitoExtension.class)
class BankTypeServiceTest {

    @Mock
    private BankTypeRepository bankTypeRepository;

    @InjectMocks
    private BankTypeService bankTypeService;


    @Test
    @DisplayName("은행 조회 - 페이지별로 정보 가져오기")
    void selectBanks() {
        // Given
        Pageable pageable = PageRequest.of(0, 3);
        List<SelectAllBanksResponseDto> selectAllBanksResponseDtos = List.of(
            new SelectAllBanksResponseDto("KB", "국민은행"),
            new SelectAllBanksResponseDto("SHIN", "신한은행"),
            new SelectAllBanksResponseDto("HANA", "하나은행"),
            new SelectAllBanksResponseDto("IBK", "IBK 기업은행"),
            new SelectAllBanksResponseDto("KG", "광주은행"),
            new SelectAllBanksResponseDto("JB", "전북은행")
        );

        Page<SelectAllBanksResponseDto> expectedPage =
            new PageImpl<>(selectAllBanksResponseDtos, pageable, selectAllBanksResponseDtos.size());
        when(bankTypeRepository.lookupBanksPage(pageable)).thenReturn(expectedPage);

        // When
        Page<SelectAllBanksResponseDto> resultPages = bankTypeService.selectBanks(pageable);

        // Then
        assertThat(resultPages.getContent().get(0).getBankTypeCode()).isEqualTo(expectedPage.getContent().get(0).getBankTypeCode());
        assertThat(resultPages.getContent().get(1).getBankTypeCode()).isEqualTo(expectedPage.getContent().get(1).getBankTypeCode());
        assertThat(resultPages.getContent().get(2).getBankTypeCode()).isEqualTo(expectedPage.getContent().get(2).getBankTypeCode());

        assertThat(resultPages.getTotalElements()).isEqualTo(expectedPage.getTotalElements());


        verify(bankTypeRepository, times(1)).lookupBanksPage(any(Pageable.class));
    }

    @Test
    @DisplayName("은행 추가 - 성공")
    void create_bank() {
        CreateBankRequestDto bankType = ReflectionUtils.newInstance(CreateBankRequestDto.class);
        ReflectionTestUtils.setField(bankType, "bankCode", "SHIN");
        ReflectionTestUtils.setField(bankType, "bankName", "신한은행");

        when(bankTypeRepository.existsById(bankType.getBankCode())).thenReturn(false);
        when(bankTypeRepository.existsByDescription(bankType.getBankName())).thenReturn(false);

        bankTypeService.createBank(bankType);

        verify(bankTypeRepository, times(1)).existsById(anyString());
        verify(bankTypeRepository, times(1)).existsByDescription(anyString());
        verify(bankTypeRepository, times((1))).save(any(BankType.class));
    }

    @Test
    @DisplayName("은행 추가 - 은행 코드 중복으로 실패")
    void create_bank_fail_code() {
        CreateBankRequestDto bankType = ReflectionUtils.newInstance(CreateBankRequestDto.class);
        ReflectionTestUtils.setField(bankType, "bankCode", "SHIN");
        ReflectionTestUtils.setField(bankType, "bankName", "신한은행");

        when(bankTypeRepository.existsById(bankType.getBankCode())).thenReturn(true);

        assertThatThrownBy(() -> bankTypeService.createBank(bankType))
            .isInstanceOf(DuplicatedBankException.class)
            .hasMessageContaining("SHIN은 이미 등록되어 있습니다.");

        verify(bankTypeRepository, times(1)).existsById(anyString());
        verify(bankTypeRepository, times((0))).save(any(BankType.class));
    }

    @Test
    @DisplayName("은행 추가 - 은행 이름 중복으로 실패")
    void create_bank_fail_name() {
        CreateBankRequestDto bankType = ReflectionUtils.newInstance(CreateBankRequestDto.class);
        ReflectionTestUtils.setField(bankType, "bankCode", "SHIN");
        ReflectionTestUtils.setField(bankType, "bankName", "신한은행");

        when(bankTypeRepository.existsById(bankType.getBankCode())).thenReturn(false);
        when(bankTypeRepository.existsByDescription(bankType.getBankName())).thenReturn(true);

        assertThatThrownBy(() -> bankTypeService.createBank(bankType))
            .isInstanceOf(DuplicatedBankException.class)
            .hasMessageContaining("신한은행은 이미 등록되어 있습니다.");

        verify(bankTypeRepository, times(1)).existsById(anyString());
        verify(bankTypeRepository, times(1)).existsByDescription(anyString());
        verify(bankTypeRepository, times((0))).save(any(BankType.class));
    }

    @Test
    @DisplayName("은행정보 리스트 조회 - 성공")
    void selectBanksForUser() {
        // Given
        List<SelectAllBanksResponseDto> selectAllBanksResponseDtos = List.of(
            new SelectAllBanksResponseDto("IBK", "IBK 기업은행"),
            new SelectAllBanksResponseDto("KG", "광주은행"),
            new SelectAllBanksResponseDto("KB", "국민은행"),
            new SelectAllBanksResponseDto("SHIN", "신한은행"),
            new SelectAllBanksResponseDto("JB", "전북은행"),
            new SelectAllBanksResponseDto("HANA", "하나은행")
        );
        when(bankTypeRepository.findAllByOrderByDescriptionAsc()).thenReturn(selectAllBanksResponseDtos);

        // When
        List<SelectAllBanksResponseDto> result = bankTypeService.selectBanksForUser();

        // Then
        assertThat(result).isEqualTo(selectAllBanksResponseDtos);

        verify(bankTypeRepository, times(1)).findAllByOrderByDescriptionAsc();
    }
}
