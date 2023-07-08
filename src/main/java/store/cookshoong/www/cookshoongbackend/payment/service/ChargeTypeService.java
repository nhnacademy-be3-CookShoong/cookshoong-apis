package store.cookshoong.www.cookshoongbackend.payment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.payment.exception.ChargeTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreateTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.request.ModifyTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;
import store.cookshoong.www.cookshoongbackend.payment.repository.chargetype.ChargeTypeRepository;

/**
 * 결제 타입에 대한 Service.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ChargeTypeService {

    private final ChargeTypeRepository chargeTypeRepository;

    /**
     * 결제 타입을 만드는 생성 메서드.
     *
     * @param requestDto    결제 타입 생성에 name 에 대한 Dto
     */
    public void createChargeType(CreateTypeRequestDto requestDto) {

        ChargeType chargeType = new ChargeType(requestDto.getName());

        chargeTypeRepository.save(chargeType);
    }

    /**
     * 결제 타입에 name 수정 메서드.
     *
     * @param chargeTypeId  결제 타입 아이디
     * @param requestDto    결제 타입 수정에 name에 대한 Dto
     */
    public void modifyChargeType(Long chargeTypeId, ModifyTypeRequestDto requestDto) {

        ChargeType chargeType = chargeTypeRepository.findById(chargeTypeId)
            .orElseThrow(ChargeTypeNotFoundException::new);

        chargeType.modifyChargeType(requestDto);
    }

    /**
     * 해당 결제 타입 아이디에 대한 조회.
     *
     * @param chargeTypeId  결제 타입 아이디
     * @return              결제 타입 name 을 반환
     */
    @Transactional(readOnly = true)
    public TypeResponseDto selectChargeType(Long chargeTypeId) {

        ChargeType chargeType = chargeTypeRepository.findById(chargeTypeId)
            .orElseThrow(ChargeTypeNotFoundException::new);

        return new TypeResponseDto(chargeType.getId(), chargeType.getName());
    }

    /**
     * 모든 결제 타입에 대한 조회.
     *
     * @return          모든 결제 타입에 대한 name을 반환
     */
    @Transactional(readOnly = true)
    public List<TypeResponseDto> selectChargeTypeAll() {

        return chargeTypeRepository.lookupChargeTypeAll();
    }

    /**
     * 환불 타입 아이디에 대한 삭제 메서드.
     *
     * @param chargeTypeId  결제 타입 아이디
     */
    public void removeChargeType(Long chargeTypeId) {

        ChargeType chargeType = chargeTypeRepository.findById(chargeTypeId)
            .orElseThrow(ChargeTypeNotFoundException::new);

        chargeTypeRepository.delete(chargeType);
    }
}
