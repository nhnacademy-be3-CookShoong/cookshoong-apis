package store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup;

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
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateOptionGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

/**
 * 옵션 그룹 엔티티.
 *
 * @author papel (윤동현)
 * @since 2023.07.11
 */
@Getter
@Entity
@Table(name = "option_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_group_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "min_select_count")
    private Integer minSelectCount;

    @Column(name = "max_select_count")
    private Integer maxSelectCount;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    /**
     * 옵션 그룹 엔티티 생성자.
     *
     * @param store          매장
     * @param name           이름
     * @param minSelectCount 최소 선택수
     * @param maxSelectCount 최대 선택수
     * @param isDeleted      삭제여부
     */
    public OptionGroup(Store store, String name, Integer minSelectCount, Integer maxSelectCount, Boolean isDeleted) {
        this.store = store;
        this.name = name;
        this.minSelectCount = minSelectCount;
        this.maxSelectCount = maxSelectCount;
        this.isDeleted = isDeleted;
    }

    /**
     * 옵션 그룹의 삭제여부 변경.
     *
     * @param isDeleted 옵션 그룹 삭제 여부
     */
    public void modifyOptionGroupIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 옵션 그룹 내용 변경.
     *
     * @param createOptionGroupRequestDto 옵션 그룹 내용 변경 Dto
     */
    public void modifyOptionGroup(CreateOptionGroupRequestDto createOptionGroupRequestDto) {
        this.name = createOptionGroupRequestDto.getName();
        this.minSelectCount = createOptionGroupRequestDto.getMinSelectCount();
        this.maxSelectCount = createOptionGroupRequestDto.getMaxSelectCount();
    }
}
