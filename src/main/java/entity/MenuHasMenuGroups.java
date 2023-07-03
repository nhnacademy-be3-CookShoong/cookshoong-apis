package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "menu_has_menu_groups")
public class MenuHasMenuGroups implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "menu_group_id", nullable = false)
    private Long menuGroupId;

    @Id
    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "menu_sequence", nullable = false)
    private Integer menuSequence;

}
