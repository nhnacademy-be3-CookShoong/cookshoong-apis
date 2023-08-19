package store.cookshoong.www.cookshoongbackend.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewStoreResponseDto;

/**
 * 사용자 리뷰조회, 사업자 리뷰조회 관리 repository.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@NoRepositoryBean
public interface ReviewRepositoryCustom {

    /**
     * 리뷰의 사진, 메뉴, 사장님 답글을 한번에 들고오는 메서드.
     *
     * @param accountId the account id
     * @param pageable  the pageable
     * @return the page
     */
    Page<SelectReviewResponseDto> lookupReviewByAccount(Long accountId, Pageable pageable);

    /**
     * 매장에서 사용자에 대한 리뷰와 답글을 가져오는 메서드.
     *
     * @param storeId the store id
     * @param pageable  the pageable
     * @return the page
     */
    Page<SelectReviewStoreResponseDto> lookupReviewByStore(Long storeId, Pageable pageable);
}
