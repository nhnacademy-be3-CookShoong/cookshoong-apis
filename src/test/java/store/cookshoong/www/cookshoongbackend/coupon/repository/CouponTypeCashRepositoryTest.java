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
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@DataJpaTest
@Import(TestEntity.class)
class CouponTypeCashRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    CouponTypeCashRepository couponTypeCashRepository;

    @Autowired
    TestEntity testEntity;

    @Test
    @DisplayName("할인금액 및 최소금액으로 쿠폰 타입 획득 - 성공")
    void findByDiscountAmountAndMinimumPriceSuccess() throws Exception {
        CouponTypeCash couponTypeCash = testEntity.getCouponTypeCash_1000_10000();
        couponTypeCashRepository.save(couponTypeCash);

        assertDoesNotThrow(() ->
            couponTypeCashRepository.findByDiscountAmountAndMinimumOrderPrice(
                couponTypeCash.getDiscountAmount(), couponTypeCash.getMinimumOrderPrice()))
            .orElseThrow(IllegalArgumentException::new);
    }

    @Test
    @DisplayName("할인금액 및 최소금액으로 쿠폰 타입 획득 - 실패")
    void findByDiscountAmountAndMinimumPriceFail() throws Exception {
        CouponTypeCash couponTypeCash = testEntity.getCouponTypeCash_1000_10000();
        Optional<CouponTypeCash> byDiscountAmountAndMinimumPrice =
            couponTypeCashRepository.findByDiscountAmountAndMinimumOrderPrice(
                couponTypeCash.getDiscountAmount(), couponTypeCash.getMinimumOrderPrice());

        assertThrows(IllegalArgumentException.class,
            () -> byDiscountAmountAndMinimumPrice.orElseThrow(IllegalArgumentException::new));
    }

}
