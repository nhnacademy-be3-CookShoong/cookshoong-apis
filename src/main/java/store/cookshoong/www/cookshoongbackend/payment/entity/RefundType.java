package store.cookshoong.www.cookshoongbackend.payment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.payment.model.request.ModifyTypeRequestDto;

/**
 * 환불 타입에 해당되는 Entity.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "refund_types")
public class RefundType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_type_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    public RefundType(String name) {
        this.name = name;
    }

    /**
     * 환불 타입에 대한 수정 메서드.
     *
     * @param requestDto    타입 수정에 대한 Dto
     */
    public void modifyRefundType(ModifyTypeRequestDto requestDto) {
        this.name = requestDto.getName();
    }
}
