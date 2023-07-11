package store.cookshoong.www.cookshoongbackend.payment.repository.refundtype;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.payment.entity.RefundType;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;

/**
 * 환불 타입에 ChargeType Repository 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.07
 */
@DataJpaTest
@Import(QueryDslConfig.class)
class RefundTypeRepositoryTest {

    @Autowired
    private RefundTypeRepository refundTypeRepository;

    RefundType refundType;

    @BeforeEach
    void setup() {
        refundType = new RefundType("INPERSON", "개인 변심으로 인한 환불", false);
    }

    @Test
    @DisplayName("환불 타입 등록")
    void saveRefundType() {

        RefundType actual = refundTypeRepository.save(refundType);

        assertEquals(refundType.getName(), actual.getName());
    }

    @Test
    @DisplayName("해당 아이디에 대한 환불 타입 조회")
    void getFindByRefundTypeId() {

        RefundType saveRefundType = refundTypeRepository.save(refundType);

        RefundType actual = refundTypeRepository.findById(saveRefundType.getId()).orElse(null);

        assert actual != null;
        assertEquals(saveRefundType.getName(), actual.getName());
    }

    @Test
    @DisplayName("모든 환불 타입에 대한 조회")
    void getFindChargeTypeAll() {

        RefundType saveRefundType = refundTypeRepository.save(refundType);

        List<TypeResponseDto> refundTypeList = refundTypeRepository.lookupRefundTypeAll();

        assertEquals(saveRefundType.getName(), refundTypeList.get(0).getName());
    }

}
