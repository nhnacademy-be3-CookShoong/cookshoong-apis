package store.cookshoong.www.cookshoongbackend.menu.entity.option;

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
import store.cookshoong.www.cookshoongbackend.menu.entity.optiongroup.OptionGroup;

/**
 * 옵션 엔티티.
 *
 * @author papel
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

    @Column(name = "option_sequence", nullable = false)
    private Integer optionSequence;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
}
