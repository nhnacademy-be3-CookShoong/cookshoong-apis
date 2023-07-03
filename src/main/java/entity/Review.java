package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "review")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "image")
    private String image;

    @Column(name = "written_at", nullable = false)
    private Date writtenAt;

    @Column(name = "contents", nullable = false)
    private String contents;

}
