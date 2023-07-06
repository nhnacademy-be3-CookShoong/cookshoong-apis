package store.cookshoong.www.cookshoongbackend.payment.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 결제에 해당되는 Entity.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "charges")
public class Charge {
    @Id
    @Column(name = "charge_code", nullable = false, length = 36)
    private String chargeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_type_id", nullable = false)
    private ChargeType chargeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code", nullable = false)
    private Order orderCode;

    @Column(name = "charged_at", nullable = false)
    private Date chargedAt;

    @Column(name = "charged_amount", nullable = false)
    private Integer chargedAmount;

}

