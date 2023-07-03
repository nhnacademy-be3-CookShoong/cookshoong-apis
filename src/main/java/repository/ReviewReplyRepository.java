package repository;

import store.cookshoong.www.cookshoongbackend.review.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long>, JpaSpecificationExecutor<ReviewReply> {

}