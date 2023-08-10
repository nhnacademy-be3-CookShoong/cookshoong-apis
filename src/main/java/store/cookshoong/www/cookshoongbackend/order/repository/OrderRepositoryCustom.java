package store.cookshoong.www.cookshoongbackend.order.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderInProgressDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

/**
 * 주문 Querydsl interface.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@NoRepositoryBean
public interface OrderRepositoryCustom {
    /**
     * 처리중인 주문을 확인하는 메서드.
     *
     * @param store           the store
     * @param orderStatusCode the order status code
     * @return the list
     */
    List<LookupOrderInProgressDto> lookupOrderInStatus(Store store, Set<String> orderStatusCode);
}
