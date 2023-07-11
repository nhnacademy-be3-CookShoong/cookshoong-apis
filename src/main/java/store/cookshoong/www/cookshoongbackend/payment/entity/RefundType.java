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
    @Column(name = "refund_type_code", nullable = false)
    private String id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    /**
     * 환불 타입 name 에 대한 생성.
     *
     * @param id        the id
     * @param name      환불 타입 이름
     * @param isDeleted the is deleted
     */
    public RefundType(String id, String name, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.isDeleted = isDeleted;
    }

    /**
     * 환불 타입에 대한 수정 메서드.
     *
     * @param requestDto 타입 수정에 대한 Dto
     */
    public void modifyRefundType(ModifyTypeRequestDto requestDto) {
        this.name = requestDto.getName();
    }

    /**
     * 호출 시 해당 타입 상태 변경됨.
     *
     * @param isDeleted the is deleted
     */
    public void modifyDeleteType(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
