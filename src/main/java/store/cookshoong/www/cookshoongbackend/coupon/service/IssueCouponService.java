package store.cookshoong.www.cookshoongbackend.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponOverCountException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;

/**
 * 쿠폰 발급 서비스.
 *
 * @author eora21 (김주호)
 * @since 2023.07.17
 */
@Service
@Transactional
@RequiredArgsConstructor
public class IssueCouponService {
    private final IssueCouponRepository issueCouponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    /**
     * 쿠폰 발행 메서드.
     * 요청된 개수만큼 발행시킨다.
     *
     * @param createIssueCouponRequestDto the issue coupon request dto
     */
    public void createIssueCoupon(CreateIssueCouponRequestDto createIssueCouponRequestDto) {
        CouponPolicy couponPolicy = couponPolicyRepository.findById(createIssueCouponRequestDto.getCouponPolicyId())
            .orElseThrow(CouponPolicyNotFoundException::new);

        couponPolicy.getCouponUsage().limitCount()
            .ifPresent(limitCount -> checkUnclaimedCouponCount(couponPolicy.getId(),
                createIssueCouponRequestDto.getIssueQuantity(), limitCount));

        for (long i = 0; i < createIssueCouponRequestDto.getIssueQuantity(); i++) {
            issueCouponRepository.save(new IssueCoupon(couponPolicy));
        }
    }

    /**
     * 유저가 아직 수령하지 않은 유효 쿠폰 개수 + 발행 요청 개수가 limitCount 초과하는지 확인.
     *
     * @param couponPolicyId 쿠폰 정책 id
     * @param issueQuantity 발행 요청 개수
     * @param limitCount 쿠폰 사용처 발행 제한값
     */
    private void checkUnclaimedCouponCount(Long couponPolicyId, Long issueQuantity, int limitCount) {
        Long unclaimedCouponCount = couponPolicyRepository.lookupUnclaimedCouponCount(couponPolicyId);

        if (limitCount < unclaimedCouponCount + issueQuantity) {
            throw new IssueCouponOverCountException(limitCount);
        }
    }
}
