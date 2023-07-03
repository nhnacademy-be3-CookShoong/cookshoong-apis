package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "review_reply")
public class ReviewReply implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_reply_id", nullable = false)
    private Long reviewReplyId;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "written_at", nullable = false)
    private Date writtenAt;

    @Column(name = "contents", nullable = false)
    private String contents;

}
