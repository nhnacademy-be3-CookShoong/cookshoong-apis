package store.cookshoong.www.cookshoongbackend.payment.repository.chargetype;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;

/**
 * 결제 타입에 ChargeType Repository 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.07
 */
@DataJpaTest
@Import(QueryDslConfig.class)
class ChargeTypeRepositoryTest {

    @Autowired
    ChargeTypeRepository chargeTypeRepository;

    ChargeType chargeType;

    @BeforeEach
    void setup() {
        chargeType = new ChargeType("KAKAO", "카카오결제", false);
    }

    @Test
    @DisplayName("결제 타입 등록")
    void saveChargeType() {

        ChargeType actual = chargeTypeRepository.save(chargeType);

        assertEquals(chargeType.getName(), actual.getName());
    }

    @Test
    @DisplayName("해당 아이디에 대한 결제 타입 조회")
    void getFindByChargeTypeId() {

        ChargeType saveChargeType = chargeTypeRepository.save(chargeType);

        ChargeType actual = chargeTypeRepository.findById(saveChargeType.getId()).orElse(null);

        assert actual != null;
        assertEquals(saveChargeType.getName(), actual.getName());
    }

    @Test
    @DisplayName("모든 결제 타입에 대한 조회")
    void getFindChargeTypeAll() {

        ChargeType saveChargeType = chargeTypeRepository.save(chargeType);

        List<TypeResponseDto> chargeTypeList = chargeTypeRepository.lookupChargeTypeAll();

        assertEquals(saveChargeType.getName(), chargeTypeList.get(0).getName());
    }
}
