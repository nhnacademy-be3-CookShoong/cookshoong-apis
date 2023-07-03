package store.cookshoong.www.cookshoongbackend.order;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "option_groups")
public class OptionGroups implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_group_id", nullable = false)
    private Integer optionGroupId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "min_select_count")
    private Integer minSelectCount;

    @Column(name = "max_select_count")
    private Integer maxSelectCount;

}
