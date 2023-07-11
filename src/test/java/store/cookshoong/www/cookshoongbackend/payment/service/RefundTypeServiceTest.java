package store.cookshoong.www.cookshoongbackend.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.payment.entity.RefundType;
import store.cookshoong.www.cookshoongbackend.payment.exception.RefundTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreateTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.request.ModifyTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;
import store.cookshoong.www.cookshoongbackend.payment.repository.refundtype.RefundTypeRepository;

/**
 * 환불 타입에 대한 Service 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.07
 */
@ExtendWith(MockitoExtension.class)
class RefundTypeServiceTest {

    @InjectMocks
    private RefundTypeService refundTypeService;

    @Mock
    private RefundTypeRepository refundTypeRepository;

    @Test
    @DisplayName("환불 타입 생성")
    void createRefundType() {

        CreateTypeRequestDto createTypeRequestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);
        ReflectionTestUtils.setField(createTypeRequestDto, "name", "개인적인 이유로 인한 환불");

        when(refundTypeRepository.save(any(RefundType.class))).thenReturn(mock(RefundType.class));

        refundTypeService.createRefundType(createTypeRequestDto);

        verify(refundTypeRepository, times(1)).save(any(RefundType.class));
    }

    @Test
    @DisplayName("환불 타입 수정")
    void modifyRefundType() {
        RefundType refundType = new RefundType("ALLREFUND", "전액환불", false);

        String refundTypeId = "INPERSON";

        ModifyTypeRequestDto modifyTypeRequestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(modifyTypeRequestDto, "name", "개인적인 이유로 인한 환불");

        when(refundTypeRepository.findById(refundTypeId)).thenReturn(Optional.of(refundType));

        refundTypeService.modifyRefundType(refundTypeId, modifyTypeRequestDto);

        verify(refundTypeRepository, times(1)).findById(refundTypeId);

        assertEquals(modifyTypeRequestDto.getName(), refundType.getName());
    }

    @Test
    @DisplayName("환불 타입 수정: 아이디가 존재하지 않을 때")
    void modifyChargeTypeChargeTypeIdNotFound() {
        String nonExistChargeTypeId = "NOTYPE";

        ModifyTypeRequestDto modifyTypeRequestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(modifyTypeRequestDto, "name", "만나서결제");

        when(refundTypeRepository.findById(nonExistChargeTypeId)).thenReturn(Optional.empty());

        assertThrows(RefundTypeNotFoundException.class,
            () -> refundTypeService.modifyRefundType(nonExistChargeTypeId, modifyTypeRequestDto));

        verify(refundTypeRepository, times(1)).findById(nonExistChargeTypeId);
        verify(refundTypeRepository, never()).save(any(RefundType.class));
    }

    @Test
    @DisplayName("해당 아이디에 대한 환불 타입 조회")
    void selectRefundType() {
        RefundType refundType = new RefundType("ALLREFUND", "전액환불", false);

        String refundTypeId = "ALLREFUND";

        when(refundTypeRepository.findById(refundTypeId)).thenReturn(Optional.of(refundType));

        TypeResponseDto responseDto = refundTypeService.selectRefundType(refundTypeId);

        verify(refundTypeRepository, times(1)).findById(refundTypeId);

        assertEquals(refundType.getName(), responseDto.getName());
    }

    @Test
    @DisplayName("해당 아이디에 대한 환불 타입 조회: 아이디가 존재하지 않을 때")
    void selectChargeTypeNotFound() {

        String nonExistChargeTypeId = "NOREASON";

        when(refundTypeRepository.findById(nonExistChargeTypeId)).thenReturn(Optional.empty());

        assertThrows(RefundTypeNotFoundException.class, () -> refundTypeService.selectRefundType(nonExistChargeTypeId));

        verify(refundTypeRepository, times(1)).findById(nonExistChargeTypeId);
    }

    @Test
    @DisplayName("모든 환불 타입 조회")
    void selectRefundTypeAll() {
        List<TypeResponseDto> responseDtoList = new ArrayList<>();
        responseDtoList.add(new TypeResponseDto("INPERSON", "개인적인 이유로 인한 환불"));

        when(refundTypeRepository.lookupRefundTypeAll()).thenReturn(responseDtoList);

        List<TypeResponseDto> actual = refundTypeService.selectRefundTypeAll();

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(responseDtoList.get(0).getName(), actual.get(0).getName());

        verify(refundTypeRepository, times(1)).lookupRefundTypeAll();
    }

    @Test
    @DisplayName("해당 아이디에 대한 환불 타입 삭제")
    void removeRefundType() {
        RefundType refundType = new RefundType("ALLREFUND", "전액환불", false);

        String refundTypeId = "ALLREFUND";

        when(refundTypeRepository.findById(refundTypeId)).thenReturn(Optional.of(refundType));

        refundTypeService.removeRefundType(refundTypeId);

        verify(refundTypeRepository, times(1)).findById(refundTypeId);
        verify(refundTypeRepository, times(1)).delete(refundType);
    }
}
