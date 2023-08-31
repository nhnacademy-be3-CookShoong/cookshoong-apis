package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectOwnCouponResponseDto;

/**
 * QueryDSL 사용을 위한 interface.
 *
 * @author eora21
 * @since 2023.07.06
 */
@NoRepositoryBean
public interface IssueCouponRepositoryCustom {
    /**
     * 소유한 모든 쿠폰 목록을 출력하는 메서드.
     *
     * @param accountId the account id
     * @param pageable  페이징 설정
     * @param usable    the usable
     * @param storeId   the store id
     * @return 소유 쿠폰 중 사용 가능 등 필터링한 결과
     */
    Page<SelectOwnCouponResponseDto> lookupAllOwnCoupons(Long accountId, Pageable pageable, Boolean usable,
                                                         Long storeId);

    /**
     * 쿠폰을 발급해주는 메서드.
     *
     * @param issueCoupon the issue coupon
     * @param account     the account
     */
    void provideCouponToAccount(IssueCoupon issueCoupon, Account account);


    /**
     * 이전에 해당 쿠폰을 받았는지의 여부를 알려주는 메서드.
     *
     * @param couponPolicyId the coupon policy id
     * @param accountId      the account id
     * @param usagePeriod    the usage period
     * @return the boolean
     */
    boolean isReceivedBefore(Long couponPolicyId, Long accountId, Integer usagePeriod);

    /**
     * 발급되지 않은 쿠폰들의 UUID를 모두 가져오는 메서드.
     *
     * @return the set
     */
    Set<UUID> lookupUnclaimedCouponCodes(Long couponPolicyId);
}
