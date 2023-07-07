package store.cookshoong.www.cookshoongbackend.payment.repository.chargetype;

import java.util.List;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;

/**
 * 결제 타입에 해당되는 Custom interface.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
public interface ChargeTypeRepositoryCustom {

    /**
     * 결제 타입에 대한 모든 것을 조회하는 메서드.
     *
     * @return      모든 결제 타입에 name 을 반환
     */
    List<TypeResponseDto> lookupChargeTypeAll();
}
