package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "menus_has_option_groups")
public class MenusHasOptionGroups implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Id
    @Column(name = "option_group_id", nullable = false)
    private Integer optionGroupId;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "option_sequence", nullable = false)
    private Integer optionSequence;

}
