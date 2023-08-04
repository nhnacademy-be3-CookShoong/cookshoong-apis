package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectOwnCouponResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponTypeConverter;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@Import(CouponTypeConverter.class)
@ExtendWith(MockitoExtension.class)
class CouponSearchServiceTest {
    @InjectMocks
    CouponSearchService couponSearchService;
    @Mock
    IssueCouponRepository issueCouponRepository;
    @Spy
    TestEntity te;
    @InjectMocks
    TestPersistEntity tpe;

    @Test
    @DisplayName("쿠폰 전체 조회 테스트")
    void getOwnCouponsTest() throws Exception {
        List<SelectOwnCouponResponseDto> ownCouponResponses = List.of(
            new SelectOwnCouponResponseDto(UUID.randomUUID(), te.getCouponTypeCash_1000_10000(),
                te.getCouponUsageStore(tpe.getOpenStore()), "매장 금액 쿠폰", "10000원 이상 시 1000원 할인",
                LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), te.getCouponTypePercent_3_1000_10000(),
                te.getCouponUsageStore(tpe.getOpenStore()), "매장 퍼센트 쿠폰",
                "10000원 이상 시 3%, 최대 1000원 할인", LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), te.getCouponTypeCash_1000_10000(),
                te.getCouponUsageMerchant(te.getMerchant()), "가맹점 금액 쿠폰", "10000원 이상 시 1000원 할인",
                LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), te.getCouponTypePercent_3_1000_10000(),
                te.getCouponUsageMerchant(te.getMerchant()), "가맹점 퍼센트 쿠폰",
                "10000원 이상 시 3%, 최대 1000원 할인", LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), te.getCouponTypeCash_1000_10000(),
                te.getCouponUsageAll(), "전체 금액 쿠폰", "10000원 이상 시 1000원 할인",
                LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), te.getCouponTypePercent_3_1000_10000(),
                te.getCouponUsageAll(), "전체 금액 쿠폰", "10000원 이상 시 3%, 최대 1000원 할인",
                LocalDate.now(), null)
        );

        when(issueCouponRepository.lookupAllOwnCoupons(
            any(Long.class), any(Pageable.class), any(), any()))
            .thenAnswer(invocation ->
                new PageImpl<>(ownCouponResponses, invocation.getArgument(1), ownCouponResponses.size()));

        Page<SelectOwnCouponResponseDto> ownCoupons =
            couponSearchService.getOwnCoupons(Long.MIN_VALUE, Pageable.ofSize(10), null, null);

        assertThat(ownCoupons).hasSize(ownCouponResponses.size());

        Iterator<SelectOwnCouponResponseDto> ownCouponIterator = ownCoupons.iterator();
        Iterator<SelectOwnCouponResponseDto> tempIterator = ownCouponResponses.iterator();

        while (ownCouponIterator.hasNext()) {
            SelectOwnCouponResponseDto ownCoupon = ownCouponIterator.next();
            SelectOwnCouponResponseDto tempCoupon = tempIterator.next();

            assertThat(ownCoupon.getIssueCouponCode()).isEqualTo(tempCoupon.getIssueCouponCode());
            assertThat(ownCoupon.getCouponTypeResponse()).isEqualTo(tempCoupon.getCouponTypeResponse());
            assertThat(ownCoupon.getCouponUsageName()).isEqualTo(tempCoupon.getCouponUsageName());
            assertThat(ownCoupon.getName()).isEqualTo(tempCoupon.getName());
            assertThat(ownCoupon.getDescription()).isEqualTo(tempCoupon.getDescription());
            assertThat(ownCoupon.getExpirationDate()).isEqualTo(tempCoupon.getExpirationDate());
            assertThat(ownCoupon.getLogTypeDescription()).isEqualTo(tempCoupon.getLogTypeDescription());
        }
    }
}
