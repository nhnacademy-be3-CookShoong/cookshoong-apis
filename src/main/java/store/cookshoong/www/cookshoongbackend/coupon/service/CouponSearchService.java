package store.cookshoong.www.cookshoongbackend.coupon.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectOwnCouponResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.temp.SelectOwnCouponResponseTempDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeResponse;
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
     * @param pageable  페이징 설정
     * @param usable    사용 가능 여부 컨디션(null = 사용 가능•불가 쿠폰 모두, true = 사용 가능, false = 사용 불가)
     * @param storeId   the store id
     * @return 소유 쿠폰 중 사용 가능 등 필터링한 결과
     */
    public Page<SelectOwnCouponResponseDto> getOwnCoupons(Long accountId, Pageable pageable, Boolean usable,
                                                          Long storeId) {
        Page<SelectOwnCouponResponseTempDto> couponResponseTemps =
            issueCouponRepository.lookupAllOwnCoupons(accountId, pageable, usable, storeId);

        List<SelectOwnCouponResponseDto> couponResponses = couponResponseTemps.stream()
            .map(this::tempToPermanent)
            .collect(Collectors.toList());

        return new PageImpl<>(
            couponResponses, couponResponseTemps.getPageable(), couponResponseTemps.getTotalElements());
    }

    private SelectOwnCouponResponseDto tempToPermanent(SelectOwnCouponResponseTempDto selectOwnCouponResponseTempDto) {
        CouponTypeResponse couponTypeResponse =
            couponTypeConverter.convert(selectOwnCouponResponseTempDto.getCouponType());
        String couponUsageName = couponUsageConverter.convert(selectOwnCouponResponseTempDto.getCouponUsage());

        return new SelectOwnCouponResponseDto(
            selectOwnCouponResponseTempDto.getIssueCouponCode(), couponTypeResponse, couponUsageName,
            selectOwnCouponResponseTempDto.getName(), selectOwnCouponResponseTempDto.getDescription(),
            selectOwnCouponResponseTempDto.getExpirationAt(), selectOwnCouponResponseTempDto.getLogTypeDescription());
    }
}
