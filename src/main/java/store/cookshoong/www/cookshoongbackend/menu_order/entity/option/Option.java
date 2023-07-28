package store.cookshoong.www.cookshoongbackend.menu_order.entity.option;

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
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;

/**
 * 옵션 엔티티.
 *
 * @author papel (윤동현)
 * @since 2023.07.11
 */
@Getter
@Entity
@Table(name = "options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_group_id", nullable = false)
    private OptionGroup optionGroup;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "option_sequence", nullable = false)
    private Integer optionSequence;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    /**
     * 옵션 엔티티 생성자.
     *
     * @param optionGroup    옵션 그룹
     * @param name           이름
     * @param price          가격
     * @param optionSequence 순서
     * @param isDeleted      삭제여부
     */
    public Option(OptionGroup optionGroup, String name, Integer price, Integer optionSequence, Boolean isDeleted) {
        this.optionGroup = optionGroup;
        this.name = name;
        this.price = price;
        this.optionSequence = optionSequence;
        this.isDeleted = isDeleted;
    }

    /**
     * 옵션의 삭제여부 변경.
     *
     * @param isDeleted 옵션 삭제 여부
     */
    public void modifyOptionIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
