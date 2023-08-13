package store.cookshoong.www.cookshoongbackend.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewResponseDto;

/**
 * 사용자 리뷰조회, 사업자 리뷰조회 관리 repository.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@NoRepositoryBean
public interface ReviewRepositoryCustom {
    Page<SelectReviewResponseDto> lookupReviewByAccount(Long accountId, Pageable pageable);
}
