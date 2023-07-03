package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "stores_has_catergories")
public class StoresHasCatergories implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Id
    @Column(name = "category_code", nullable = false)
    private String categoryCode;

}
