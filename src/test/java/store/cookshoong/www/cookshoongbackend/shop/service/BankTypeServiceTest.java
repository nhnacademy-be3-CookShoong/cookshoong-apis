package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.exception.banktype.DuplicatedBankException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateBankRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.bank.BankTypeRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

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
    void selectBanksForUser() {
    }
}
