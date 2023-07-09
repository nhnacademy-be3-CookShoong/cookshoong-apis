package store.cookshoong.www.cookshoongbackend.coupon.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.CouponResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.CouponTypeResponse;
import store.cookshoong.www.cookshoongbackend.coupon.model.temp.CouponResponseTempDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponTypeConverter;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponUsageConverter;

/**
 * 쿠폰 조회 서비스.
 *
 * @author eora21
 * @since 2023.07.06
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponSearchService {
    private final IssueCouponRepository issueCouponRepository;
    private final CouponTypeConverter couponTypeConverter;
    private final CouponUsageConverter couponUsageConverter;

    /**
     * 사용자 소유 쿠폰을 탐색 후 반환하는 메서드.
     *
     * @param accountId the account id
     * @return the own coupons
     */
    public List<CouponResponseDto> getOwnCoupons(Long accountId) {
        return issueCouponRepository.lookupAllOwnCoupons(accountId)
            .stream()
            .map(this::tempToPermanent)
            .collect(Collectors.toList());
    }

    private CouponResponseDto tempToPermanent(CouponResponseTempDto couponResponseTempDto) {
        CouponTypeResponse couponTypeResponse = couponTypeConverter.convert(couponResponseTempDto.getCouponType());
        String couponUsageName = couponUsageConverter.convert(couponResponseTempDto.getCouponUsage());

        return new CouponResponseDto(
            couponResponseTempDto.getIssueCouponCode(), couponTypeResponse, couponUsageName,
            couponResponseTempDto.getName(), couponResponseTempDto.getDescription(),
            couponResponseTempDto.getExpirationAt());
    }
}
