package repository;

import entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long>, JpaSpecificationExecutor<ReviewReply> {

}