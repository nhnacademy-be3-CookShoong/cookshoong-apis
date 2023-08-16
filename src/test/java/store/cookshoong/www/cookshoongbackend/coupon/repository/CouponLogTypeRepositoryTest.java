package store.cookshoong.www.cookshoongbackend.coupon.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLogType;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@DataJpaTest
@Import(TestEntity.class)
class CouponLogTypeRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    CouponLogTypeRepository couponLogTypeRepository;

    @Autowired
    TestEntity testEntity;

    @BeforeEach
    void beforeEach() {
        CouponLogType couponLogType = testEntity.createUsingDeclared(CouponLogType.class);
        ReflectionTestUtils.setField(couponLogType, "code", "CANCEL");
        ReflectionTestUtils.setField(couponLogType, "description", "취소");
        couponLogTypeRepository.saveAndFlush(couponLogType);
    }

    @Test
    @DisplayName("쿠폰 로그 타입 코드로 쿠폰 로그 타입 획득")
    void findByCouponLogTypeCodeTest() throws Exception {
        Optional<CouponLogType> byCouponLogTypeCode =
            couponLogTypeRepository.findByCouponLogTypeCode(CouponLogType.Code.CANCEL);
        assertThat(byCouponLogTypeCode).isNotEmpty();
    }
}
