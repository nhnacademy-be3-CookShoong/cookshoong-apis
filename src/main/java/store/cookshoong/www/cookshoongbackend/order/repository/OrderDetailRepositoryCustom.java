package store.cookshoong.www.cookshoongbackend.order.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailResponseDto;

/**
 * 주문 상세에서 QueryDSL 사용을 위한 interface.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@NoRepositoryBean
public interface OrderDetailRepositoryCustom {
    /**
     * 포인트를 위한 주문 상세 정보 확인.
     *
     * @param orderCode the order code
     * @return the list
     */
    List<LookupOrderDetailResponseDto> lookupOrderDetailForPoint(UUID orderCode);
}
