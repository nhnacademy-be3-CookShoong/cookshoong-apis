package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "addresses")
public class Addresses implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "address_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(name = "main_place", nullable = false)
    private String mainPlace;

    @Column(name = "detail_place")
    private String detailPlace;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longtitude", nullable = false)
    private BigDecimal longtitude;

}
