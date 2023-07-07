package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.coupon.model.temp.CouponResponseTempDto;

/**
 * QueryDSL 사용을 위한 interface.
 *
 * @author eora21
 * @since 2023.07.06
 */
@NoRepositoryBean
public interface IssueCouponRepositoryCustom {
    List<CouponResponseTempDto> lookupAllOwnCoupons(Long accountId);
}
