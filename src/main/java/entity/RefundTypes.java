package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "refund_types")
public class RefundTypes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "refund_type_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundTypeId;

    @Column(name = "name", nullable = false)
    private String name;

}
