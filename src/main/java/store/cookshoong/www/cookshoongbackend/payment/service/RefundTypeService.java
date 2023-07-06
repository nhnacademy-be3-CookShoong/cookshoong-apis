package store.cookshoong.www.cookshoongbackend.payment.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.payment.entity.RefundType;
import store.cookshoong.www.cookshoongbackend.payment.exception.RefundTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreateTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.request.ModifyTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;
import store.cookshoong.www.cookshoongbackend.payment.repository.refundtype.RefundTypeRepository;

/**
 * 환불 타입에 Service.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RefundTypeService {

    private final RefundTypeRepository refundTypeRepository;

    /**
     * 환불 타입을 만드는 생성 메서드.
     *
     * @param requestDto    환불 타입 생성에 name 에 대한 Dto
     */
    public void createRefundType(CreateTypeRequestDto requestDto) {

        RefundType refundType = new RefundType(requestDto.getName());

        refundTypeRepository.save(refundType);
    }

    /**
     * 환불 타입에 name 에 수정 메서드.
     *
     * @param refundTypeId  환불 타입 아이디
     * @param requestDto    환불 타입 수정에 name에 대한 Dto
     */
    public void modifyRefundType(Long refundTypeId, ModifyTypeRequestDto requestDto) {

        RefundType refundType = refundTypeRepository.findById(refundTypeId)
            .orElseThrow(RefundTypeNotFoundException::new);

        refundType.modifyRefundType(requestDto);
    }

    /**
     * 해당 환불 타입 아이디에 대한 조회.
     *
     * @param refundTypeId  환불 타입 아이디
     * @return              환불 타입 name 을 반환
     */
    @Transactional(readOnly = true)
    public TypeResponseDto selectRefundType(Long refundTypeId) {

        RefundType refundType = refundTypeRepository.findById(refundTypeId)
            .orElseThrow(RefundTypeNotFoundException::new);

        return new TypeResponseDto(refundType.getName());
    }

    /**
     * 모든 환불 타입에 대한 조회.
     *
     * @return          모든 환불 타입에 대한 name을 반환
     */
    @Transactional(readOnly = true)
    public List<TypeResponseDto> selectRefundTypeAll() {

        return refundTypeRepository.lookupRefundTypeAll();
    }

    /**
     * 환불 타입 아이디에 대한 삭제 메서드.
     *
     * @param refundTypeId  환불 타입 아이디
     */
    public void removeRefundType(Long refundTypeId) {

        RefundType refundType = refundTypeRepository.findById(refundTypeId)
            .orElseThrow(RefundTypeNotFoundException::new);

        refundTypeRepository.delete(refundType);
    }
}
