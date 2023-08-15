package store.cookshoong.www.cookshoongbackend.review.entity;

import java.time.LocalDateTime;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.review.model.request.CreateReviewRequestDto;

/**
 * 리뷰 엔티티.
 *
 * @author seungyeon (유승연)
 * @since 2023.08.11
 */
@NoArgsConstructor
@Getter
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_code", nullable = false)
    private Order order;

    @OneToMany(mappedBy = "review", cascade = CascadeType.PERSIST)
    private final Set<ReviewHasImage> reviewHasImages = new HashSet<>();

    @Lob
    @Column(name = "contents", nullable = false)
    private String contents;


    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "written_at", nullable = false)
    private LocalDateTime writtenAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 리뷰 등록시 사용되는 생성자.
     *
     * @param order  the order code
     * @param requestDto the request dto
     */
    public Review(Order order, CreateReviewRequestDto requestDto) {
        this.order = order;
        this.contents = requestDto.getContents();
        this.rating = requestDto.getRating();
        this.writtenAt = LocalDateTime.now();
    }

}
