package store.cookshoong.www.cookshoongbackend.coupon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class CouponUsageStoreRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    CouponUsageStoreRepository couponUsageStoreRepository;

    @Test
    @DisplayName("매장 id로 쿠폰 매장 사용처 획득 - 성공")
    void findByStoreIdSuccessTest() throws Exception {
        Long storeId = 3L;
        couponUsageStoreRepository.saveAndFlush(new CouponUsageStore(storeId));

        assertDoesNotThrow(() -> couponUsageStoreRepository.findByStoreId(storeId)
            .orElseThrow(IllegalArgumentException::new));
    }

    @Test
    @DisplayName("매장 id로 쿠폰 매장 사용처 획득 - 실패")
    void findByStoreIdFailTest() throws Exception {
        Long storeId = 3L;
        Optional<CouponUsageStore> byStoreId = couponUsageStoreRepository.findByStoreId(storeId);

        assertThrows(IllegalArgumentException.class,
            () -> byStoreId.orElseThrow(IllegalArgumentException::new));
    }
}
