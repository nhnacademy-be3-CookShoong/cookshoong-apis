package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "merchants")
public class Merchants implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "merchant_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchantId;

    @Column(name = "name", nullable = false)
    private String name;

}
