package store.cookshoong.www.cookshoongbackend.coupon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class CouponTypeCashRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    CouponTypeCashRepository couponTypeCashRepository;

    int discountAmount = 1_000;

    int minimumPrice = 10_000;

    @Test
    @DisplayName("할인금액 및 최소금액으로 쿠폰 타입 획득 - 성공")
    void findByDiscountAmountAndMinimumPriceSuccess() throws Exception {
        couponTypeCashRepository.saveAndFlush(new CouponTypeCash(discountAmount, minimumPrice));

        assertDoesNotThrow(() ->
            couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(discountAmount, minimumPrice))
            .orElseThrow(IllegalArgumentException::new);
    }

    @Test
    @DisplayName("할인금액 및 최소금액으로 쿠폰 타입 획득 - 실패")
    void findByDiscountAmountAndMinimumPriceFail() throws Exception {
        Optional<CouponTypeCash> byDiscountAmountAndMinimumPrice =
            couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(discountAmount, minimumPrice);

        assertThrows(IllegalArgumentException.class,
            () -> byDiscountAmountAndMinimumPrice.orElseThrow(IllegalArgumentException::new));
    }

}
