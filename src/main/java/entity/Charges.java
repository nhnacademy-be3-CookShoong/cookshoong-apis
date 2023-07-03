package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "charges")
public class Charges implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "charge_code", nullable = false)
    private String chargeCode;

    /**
     * AI
     */
    @Column(name = "charge_type_id", nullable = false)
    private Long chargeTypeId;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "charged_at", nullable = false)
    private Date chargedAt;

    @Column(name = "charged_amount", nullable = false)
    private Long chargedAmount;

}
