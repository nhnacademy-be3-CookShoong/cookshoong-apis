package store.cookshoong.www.cookshoongbackend.order;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "options")
public class Options implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "option_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @Column(name = "option_group_id", nullable = false)
    private Integer optionGroupId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "option_sequence", nullable = false)
    private Integer optionSequence;

}
