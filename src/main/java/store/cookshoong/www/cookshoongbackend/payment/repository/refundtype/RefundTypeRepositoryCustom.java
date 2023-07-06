package store.cookshoong.www.cookshoongbackend.payment.repository.refundtype;

import java.util.List;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;

/**
 * 환불 타입에 해당되는 Custom Interface.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
public interface RefundTypeRepositoryCustom {

    /**
     * 환불 타입에 대한 모든 것을 조회하는 메서드.
     *
     * @return      모든 환불 타입에 name 을 반환
     */
    List<TypeResponseDto> lookupRefundTypeAll();
}
