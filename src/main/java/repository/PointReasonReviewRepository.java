package repository;

import entity.PointReasonReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PointReasonReviewRepository extends JpaRepository<PointReasonReview, Long>, JpaSpecificationExecutor<PointReasonReview> {

}