package store.cookshoong.www.cookshoongbackend.point.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.point.exception.LowPointException;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointResponseDto;
import store.cookshoong.www.cookshoongbackend.point.repository.PointLogRepository;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {
    @InjectMocks
    PointService pointService;
    @Mock
    AccountRepository accountRepository;
    @Mock
    PointLogRepository pointLogRepository;

    @Test
    @DisplayName("포인트 총합 실패 - 사용자 없음")
    void selectSumPointAccountNotFoundFailTest() throws Exception {
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () ->
            pointService.selectSumPoint(Long.MIN_VALUE));
    }

    @Test
    @DisplayName("포인트 총합 성공")
    void selectSumPointSuccessTest() throws Exception {
        Account account = mock(Account.class);

        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(account));

        when(pointLogRepository.lookupMyPoint(account))
            .thenReturn(new PointResponseDto(Integer.MAX_VALUE));

        assertDoesNotThrow(() ->
            pointService.selectSumPoint(Long.MIN_VALUE));
    }

    @Test
    @DisplayName("포인트 로그 페이지 실패 - 사용자 없음")
    void selectAllPointLogAccountNotFoundFailTest() throws Exception {
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () ->
            pointService.selectAllPointLog(Long.MIN_VALUE, Pageable.ofSize(20)));
    }

    @Test
    @DisplayName("포인트 로그 페이지 성공")
    void selectAllPointLogAccountSuccessTest() throws Exception {
        Account account = mock(Account.class);

        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(account));

        assertDoesNotThrow(() ->
            pointService.selectAllPointLog(Long.MIN_VALUE, Pageable.ofSize(20)));
    }

    @Test
    @DisplayName("포인트 검증 실패 - 사용자 없음")
    void getValidPointNonAccountFailTest() throws Exception {
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () ->
            pointService.getValidPoint(Long.MIN_VALUE, 1_000, 10_000));
    }

    @Test
    @DisplayName("포인트 검증 실패 - 소유 포인트보다 많은 포인트 사용")
    void getValidPointLowPointFailTest() throws Exception {
        Account account = mock(Account.class);
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(account));

        PointResponseDto pointResponseDto = mock(PointResponseDto.class);
        when(pointLogRepository.lookupMyPoint(account))
            .thenReturn(pointResponseDto);

        when(pointResponseDto.getPoint())
            .thenReturn(0);

        assertThrowsExactly(LowPointException.class, () ->
            pointService.getValidPoint(Long.MIN_VALUE, 1_000, 10_000));
    }

    @Test
    @DisplayName("포인트 검증 성공 - 주문보다 적은 포인트")
    void getValidPointSuccessTest() throws Exception {
        Account account = mock(Account.class);
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(account));

        PointResponseDto pointResponseDto = mock(PointResponseDto.class);
        when(pointLogRepository.lookupMyPoint(account))
            .thenReturn(pointResponseDto);

        when(pointResponseDto.getPoint())
            .thenReturn(1_000);

        assertThat(pointService.getValidPoint(Long.MIN_VALUE, 1_000, 10_000))
            .isEqualTo(1_000);
    }

    @Test
    @DisplayName("포인트 검증 성공 - 주문보다 많은 포인트")
    void getValidPointSuccessMathMinTest() throws Exception {
        Account account = mock(Account.class);
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(account));

        PointResponseDto pointResponseDto = mock(PointResponseDto.class);
        when(pointLogRepository.lookupMyPoint(account))
            .thenReturn(pointResponseDto);

        when(pointResponseDto.getPoint())
            .thenReturn(100_000);

        assertThat(pointService.getValidPoint(Long.MIN_VALUE, 100_000, 10_000))
            .isEqualTo(10_000);
    }
}

