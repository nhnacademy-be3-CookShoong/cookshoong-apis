package store.cookshoong.www.cookshoongbackend.coupon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    /**
     * 소유한 모든 쿠폰 목록을 출력하는 메서드.
     *
     * @param accountId the account id
     * @param pageable  페이징 설정
     * @param useCond   사용 가능 여부 컨디션(null = 조건없음, true = 사용 가능, false = 사용 불가)
     * @param storeId   the store id
     * @return 소유 쿠폰 중 사용 가능 등 필터링한 결과
     */
    Page<CouponResponseTempDto> lookupAllOwnCoupons(Long accountId, Pageable pageable, Boolean useCond, Long storeId);
}
