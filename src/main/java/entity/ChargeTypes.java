package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "charge_types")
public class ChargeTypes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "charge_type_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chargeTypeId;

    @Column(name = "name", nullable = false)
    private String name;

}
