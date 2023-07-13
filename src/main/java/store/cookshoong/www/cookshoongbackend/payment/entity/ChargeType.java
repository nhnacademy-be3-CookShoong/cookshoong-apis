package store.cookshoong.www.cookshoongbackend.payment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.payment.model.request.ModifyTypeRequestDto;

/**
 * 결제 타입에 해당되는 Entity.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "charge_types")
public class ChargeType {
    @Id
    @Column(name = "charge_type_code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    /**
     * 결제 타입 name 에 대한 생성자.
     *
     * @param code        the code
     * @param name      결제 타입 이름
     * @param isDeleted 타입 삭제 상태
     */
    public ChargeType(String code, String name, boolean isDeleted) {
        this.code = code;
        this.name = name;
        this.isDeleted = isDeleted;
    }

    /**
     * 결제 타입에 대한 수정 메서드.
     *
     * @param name 수정한 타입이름
     */
    public void modifyChargeType(String name) {
        this.name = name;
    }

    /**
     * 결제 타입에 대한 삭제 메서드.
     * 호출 시 해당 타입은 deleted 상태로 변경됨.
     *
     * @param isDeleted 삭제 상태 변경
     */
    public void modifyDeleteCharge(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
