package store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

/**
 * 메뉴 그룹 엔티티.
 *
 * @author papel (윤동현)
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

    @OneToMany(mappedBy = "menuGroup", cascade = CascadeType.PERSIST)
    private final Set<MenuHasMenuGroup> menuHasMenuGroups = new HashSet<>();

    /**
     * 메뉴 그룹 엔티티 생성자.
     *
     * @param store             매장
     * @param name              이름
     * @param description       설명
     * @param menuGroupSequence 순서
     */
    public MenuGroup(Store store, String name, String description, Integer menuGroupSequence) {
        this.store = store;
        this.name = name;
        this.description = description;
        this.menuGroupSequence = menuGroupSequence;
    }

    /**
     * 메뉴 그룹 내용 변경.
     *
     * @param createMenuGroupRequestDto 매뉴 그룹 내용 변경 Dto
     */
    public void modifyMenuGroup(CreateMenuGroupRequestDto createMenuGroupRequestDto) {
        this.name = createMenuGroupRequestDto.getName();
        this.description = createMenuGroupRequestDto.getDescription();
    }
}
