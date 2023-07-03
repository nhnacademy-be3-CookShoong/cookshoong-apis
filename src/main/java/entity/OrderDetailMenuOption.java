package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "order_detail_menu_option")
public class OrderDetailMenuOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Id
    @Column(name = "order_detail_id", nullable = false)
    private Long orderDetailId;

}
