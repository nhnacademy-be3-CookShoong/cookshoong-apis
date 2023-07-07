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
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import(TestPersistEntity.class)
class CouponUsageStoreRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    CouponUsageStoreRepository couponUsageStoreRepository;

    @Autowired
    TestPersistEntity testPersistEntity;

    @Test
    @DisplayName("매장 id로 쿠폰 매장 사용처 획득 - 성공")
    void findByStoreIdSuccessTest() throws Exception {
        Store store = testPersistEntity.getOpenStore();
        couponUsageStoreRepository.saveAndFlush(new CouponUsageStore(store));
        assertDoesNotThrow(() -> couponUsageStoreRepository.findByStoreId(store.getId())
            .orElseThrow(IllegalArgumentException::new));
    }

    @Test
    @DisplayName("매장 id로 쿠폰 매장 사용처 획득 - 실패")
    void findByStoreIdFailTest() throws Exception {
        Long storeId = Long.MIN_VALUE;
        Optional<CouponUsageStore> byStoreId = couponUsageStoreRepository.findByStoreId(storeId);

        assertThrows(IllegalArgumentException.class,
            () -> byStoreId.orElseThrow(IllegalArgumentException::new));
    }
}
