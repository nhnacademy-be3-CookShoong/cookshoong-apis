package store.cookshoong.www.cookshoongbackend.order.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupAccountOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderInStatusResponseDto;
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
     * 해당하는 상태들의 매장 주문을 확인하는 메서드.
     *
     * @param store           the store
     * @param orderStatusCode the order status code
     * @return the list
     */
    List<LookupOrderInStatusResponseDto> lookupOrderInStatus(Store store, Set<String> orderStatusCode);

    /**
     * 해당하는 상태들의 매장 주문을 확인하는 메서드. 페이징 처리가 되어 있다.
     *
     * @param store           the store
     * @param orderStatusCode the order status code
     * @param pageable        the pageable
     * @return the page
     */
    Page<LookupOrderInStatusResponseDto> lookupOrderInStatus(Store store, Set<String> orderStatusCode,
                                                             Pageable pageable);

    /**
     * 해당하는 상태들의 유저 주문을 확인하는 메서드. 페이징 처리가 되어 있다.
     *
     * @param account         the account
     * @param orderStatusCode the order status code
     * @param pageable        the pageable
     * @return the page
     */
    Page<LookupAccountOrderInStatusResponseDto> lookupOrderInStatus(Account account, Set<String> orderStatusCode,
                                                                    Pageable pageable);
}
