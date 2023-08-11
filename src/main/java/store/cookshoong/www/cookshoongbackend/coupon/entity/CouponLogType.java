package store.cookshoong.www.cookshoongbackend.coupon.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 쿠폰 내역 타입 entity. 사용, 환불 등을 표기.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupon_log_type")
public class CouponLogType {

    @Id
    @Column(name = "coupon_log_type_code", nullable = false)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

    /**
     * The enum Code.
     */
    public enum Code {
        USE,
        CANCEL
    }

}
