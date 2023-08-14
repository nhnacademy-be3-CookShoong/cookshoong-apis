package store.cookshoong.www.cookshoongbackend.review.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 답변에 대한 엔티티.
 *
 * @author seungyeon (유승연)
 * @since 2023.08.11
 */
@NoArgsConstructor
@Getter
@Entity
@Table(name = "review_reply")
public class ReviewReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_reply_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Lob
    @Column(name = "contents", nullable = false)
    private String contents;

    @Column(name = "written_at", nullable = false)
    private LocalDateTime writtenAt;

    /**
     * 리뷰 답글을 생성하는 생성자.
     *
     * @param review        the review
     * @param contents      답글 내용
     */
    public ReviewReply(Review review, String contents) {

        this.review = review;
        this.contents = contents;
        this.writtenAt = LocalDateTime.now();
    }
}
