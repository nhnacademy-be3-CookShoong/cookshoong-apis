package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.stauts.StoreStatusRepository;

/**
 * 관리자, 사업자 : 매장 상태 목록 조회 서비스 코드 테스트.
 *
 * @author seungyeon
 * @since 2023.07.21
 */
@ExtendWith(MockitoExtension.class)
class StoreStatusServiceTest {

    @Mock
    private StoreStatusRepository storeStatusRepository;

    @InjectMocks
    private StoreStatusService storeStatusService;

    @Test
    @DisplayName("매장 상태 목록 조회 - 성공")
    void select_all_status_for_user() {
        SelectAllStatusResponseDto storeOpened = new SelectAllStatusResponseDto("OPEN", "영업중");
        SelectAllStatusResponseDto storeClosed = new SelectAllStatusResponseDto("CLOSE", "휴식중");
        SelectAllStatusResponseDto storeOuted = new SelectAllStatusResponseDto("OUTED", "폐업");


        List<SelectAllStatusResponseDto> storeStatuses = Arrays.asList(storeOpened, storeClosed, storeOuted);

        when(storeStatusRepository.findAllBy()).thenReturn(storeStatuses);

        List<SelectAllStatusResponseDto> result = storeStatusService.selectAllStatusForUser();

        assertEquals(3, result.size());

        assertEquals("OPEN", result.get(0).getStoreStatusCode());
        assertEquals("영업중", result.get(0).getDescription());

        assertEquals("CLOSE", result.get(1).getStoreStatusCode());
        assertEquals("휴식중", result.get(1).getDescription());

        assertEquals("OUTED", result.get(2).getStoreStatusCode());
        assertEquals("폐업", result.get(2).getDescription());

    }
}
