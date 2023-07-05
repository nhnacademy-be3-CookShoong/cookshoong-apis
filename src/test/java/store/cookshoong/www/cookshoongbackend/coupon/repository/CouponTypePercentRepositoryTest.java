package store.cookshoong.www.cookshoongbackend.coupon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class CouponTypePercentRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    CouponTypePercentRepository couponTypePercentRepository;

    BigDecimal rate = new BigDecimal("1.5");

    int discountAmount = 1_000;

    int minimumPrice = 10_000;

    @Test
    @DisplayName("할인금액 및 최대금액, 최소금액으로 쿠폰 타입 획득 - 성공")
    void findByRateDiscountAmountAndMinimumPriceSuccess() throws Exception {
        couponTypePercentRepository.saveAndFlush(new CouponTypePercent(rate, discountAmount, minimumPrice));

        assertDoesNotThrow(() ->
            couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(rate, discountAmount, minimumPrice))
            .orElseThrow(IllegalArgumentException::new);
    }

    @Test
    @DisplayName("할인금액 및 최대금액, 최소금액으로 쿠폰 타입 획득 - 실패")
    void findByRateDiscountAmountAndMinimumPriceFail() throws Exception {
        Optional<CouponTypePercent> byRateAndMinimumPriceAndMaximumPrice =
            couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(rate, discountAmount, minimumPrice);
        assertThrows(IllegalArgumentException.class,
            () -> byRateAndMinimumPriceAndMaximumPrice.orElseThrow(IllegalArgumentException::new));
    }
}
