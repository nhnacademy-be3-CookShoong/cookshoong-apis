package store.cookshoong.www.cookshoongbackend.menu.entity.menugroup;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;

/**
 * 메뉴 그룹 엔티티.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
@Entity
@Table(name = "menu_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_group_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "menu_group_sequence", nullable = false)
    private Integer menuGroupSequence;
}
