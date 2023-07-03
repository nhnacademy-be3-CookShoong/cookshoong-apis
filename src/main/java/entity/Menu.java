package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "menu_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "image")
    private String image;

    /**
     * 분단위
     */
    @Column(name = "cooking_time", nullable = false)
    private Integer cookingTime;

    @Column(name = "earning_rate")
    private BigDecimal earningRate;

    @Column(name = "is_sold_out", nullable = false)
    private Boolean soldOut;

    @Column(name = "description")
    private String description;

}
