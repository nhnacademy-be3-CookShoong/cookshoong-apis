package store.cookshoong.www.cookshoongbackend.coupon.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@DataJpaTest
@Import(TestEntity.class)
class CouponTypePercentRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    CouponTypePercentRepository couponTypePercentRepository;

    @Autowired
    TestEntity testEntity;

    @Test
    @DisplayName("할인금액 및 최대금액, 최소금액으로 쿠폰 타입 획득 - 성공")
    void findByRateDiscountAmountAndMinimumPriceSuccess() throws Exception {
        CouponTypePercent couponTypePercent = testEntity.getCouponTypePercent_3_1000_10000();
        couponTypePercentRepository.save(couponTypePercent);

        assertDoesNotThrow(() -> couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(
            couponTypePercent.getRate(), couponTypePercent.getMinimumPrice(), couponTypePercent.getMaximumPrice()))
            .orElseThrow(IllegalArgumentException::new);
    }

    @Test
    @DisplayName("할인금액 및 최대금액, 최소금액으로 쿠폰 타입 획득 - 실패")
    void findByRateDiscountAmountAndMinimumPriceFail() throws Exception {
        CouponTypePercent couponTypePercent = testEntity.getCouponTypePercent_3_1000_10000();

        Optional<CouponTypePercent> byRateAndMinimumPriceAndMaximumPrice =
            couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(
                couponTypePercent.getRate(), couponTypePercent.getMinimumPrice(), couponTypePercent.getMaximumPrice()
            );
        assertThrows(IllegalArgumentException.class,
            () -> byRateAndMinimumPriceAndMaximumPrice.orElseThrow(IllegalArgumentException::new));
    }
}
