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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.payment.exception.ChargeTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreateTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.request.ModifyTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;
import store.cookshoong.www.cookshoongbackend.payment.repository.chargetype.ChargeTypeRepository;

/**
 * 결제 타입에 대한 Service 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.07
 */
@ExtendWith(MockitoExtension.class)
class ChargeTypeServiceTest {

    @InjectMocks
    private ChargeTypeService chargeTypeService;

    @Mock
    private ChargeTypeRepository chargeTypeRepository;

    ChargeType chargeType;

    @BeforeEach
    void setup() {
        chargeType = new ChargeType("페이코결제");
    }

    @Test
    @DisplayName("결제 타입 생성")
    void createChargeType() {
        CreateTypeRequestDto createTypeRequestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);

        ReflectionTestUtils.setField(createTypeRequestDto, "name", "토스결제");

        when(chargeTypeRepository.save(any(ChargeType.class))).thenReturn(mock(ChargeType.class));

        chargeTypeService.createChargeType(createTypeRequestDto);

        verify(chargeTypeRepository, times(1)).save(any(ChargeType.class));
    }

    @Test
    @DisplayName("결제 타입 수정")
    void modifyChargeType() {
        Long chargeTypeId = 1L;
        ModifyTypeRequestDto modifyTypeRequestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(modifyTypeRequestDto, "name", "만나서결제");

        when(chargeTypeRepository.findById(chargeTypeId)).thenReturn(Optional.of(chargeType));

        chargeTypeService.modifyChargeType(chargeTypeId, modifyTypeRequestDto);

        verify(chargeTypeRepository, times(1)).findById(chargeTypeId);

        assertEquals(modifyTypeRequestDto.getName(), chargeType.getName());
    }

    @Test
    @DisplayName("결제 타입 수정: 아이디가 존재하지 않을 때")
    void modifyChargeTypeChargeTypeIdNotFound() {
        Long nonExistChargeTypeId = 100L;

        ModifyTypeRequestDto modifyTypeRequestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(modifyTypeRequestDto, "name", "만나서결제");

        when(chargeTypeRepository.findById(nonExistChargeTypeId)).thenReturn(Optional.empty());

        assertThrows(ChargeTypeNotFoundException.class,
            () -> chargeTypeService.modifyChargeType(nonExistChargeTypeId, modifyTypeRequestDto));

        verify(chargeTypeRepository, times(1)).findById(nonExistChargeTypeId);
        verify(chargeTypeRepository, never()).save(any(ChargeType.class));
    }

    @Test
    @DisplayName("해당 아이디에 대한 결제 타입 조회")
    void selectChargeType() {

        Long chargeTypeId = 1L;

        when(chargeTypeRepository.findById(chargeTypeId)).thenReturn(Optional.of(chargeType));

        TypeResponseDto responseDto = chargeTypeService.selectChargeType(chargeTypeId);

        verify(chargeTypeRepository, times(1)).findById(chargeTypeId);

        assertEquals(chargeType.getName(), responseDto.getName());
    }

    @Test
    @DisplayName("해당 아이디에 대한 결제 타입 조회: 아이디가 존재하지 않을 때")
    void selectChargeTypeNotFound() {

        Long nonExistChargeTypeId = 100L;

        when(chargeTypeRepository.findById(nonExistChargeTypeId)).thenReturn(Optional.empty());

        assertThrows(ChargeTypeNotFoundException.class, () -> chargeTypeService.selectChargeType(nonExistChargeTypeId));

        verify(chargeTypeRepository, times(1)).findById(nonExistChargeTypeId);
    }

    @Test
    @DisplayName("모든 결제 타입 조회")
    void selectChargeTypeAll() {
        List<TypeResponseDto> chargeTypeList = new ArrayList<>();
        chargeTypeList.add(new TypeResponseDto("카드결제"));

        when(chargeTypeRepository.lookupChargeTypeAll()).thenReturn(chargeTypeList);

        List<TypeResponseDto> actual = chargeTypeService.selectChargeTypeAll();

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(chargeTypeList.get(0).getName(), actual.get(0).getName());

        verify(chargeTypeRepository, times(1)).lookupChargeTypeAll();
    }

    @Test
    @DisplayName("해당 아이디에 대한 결제 타입 삭제")
    void removeChargeType() {
        Long chargeTypeId = 1L;

        when(chargeTypeRepository.findById(chargeTypeId)).thenReturn(Optional.of(chargeType));

        chargeTypeService.removeChargeType(chargeTypeId);

        verify(chargeTypeRepository, times(1)).findById(chargeTypeId);
        verify(chargeTypeRepository, times(1)).delete(chargeType);
    }
}
