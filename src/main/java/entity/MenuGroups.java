package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "menu_groups")
public class MenuGroups implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "menu_group_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuGroupId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "menu_group_sequence", nullable = false)
    private Integer menuGroupSequence;

}
