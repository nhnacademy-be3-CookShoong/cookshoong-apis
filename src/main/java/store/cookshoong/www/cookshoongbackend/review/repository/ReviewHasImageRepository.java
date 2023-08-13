package store.cookshoong.www.cookshoongbackend.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.review.entity.ReviewHasImage;

public interface ReviewHasImageRepository extends JpaRepository<ReviewHasImage, ReviewHasImage.Pk> {
}
