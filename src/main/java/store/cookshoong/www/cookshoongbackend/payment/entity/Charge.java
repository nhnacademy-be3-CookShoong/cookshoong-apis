package store.cookshoong.www.cookshoongbackend.payment.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;

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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "charge_code", columnDefinition = "BINARY(16)", nullable = false)
    private UUID code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_type_code", nullable = false)
    private ChargeType chargeType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code", nullable = false)
    private Order order;

    @Column(name = "charged_at", nullable = false)
    private LocalDateTime chargedAt;

    @Column(name = "charged_amount", nullable = false)
    private Integer chargedAmount;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    /**
     * Charge 에 대한 생성자.
     *
     * @param chargeType        결제 타입
     * @param order             주문
     * @param chargedAt         결제 생성일
     * @param chargedAmount     결제 금액
     * @param paymentKey        결제 Key
     */
    public Charge(ChargeType chargeType, Order order,
                  LocalDateTime chargedAt, Integer chargedAmount, String paymentKey) {

        this.chargeType = chargeType;
        this.order = order;
        this.chargedAt = chargedAt;
        this.chargedAmount = chargedAmount;
        this.paymentKey = paymentKey;
    }

}

