package store.cookshoong.www.cookshoongbackend.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.store.model.response.StoreListResponseDto;

/**
 * 매장 Custom 레포지토리 인터페이스.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@NoRepositoryBean
public interface StoreRepositoryCustom {

    /**
     * 사업자 회원의 매장 조회시 pagination 구현.
     *
     * @param accountId 회원 아이디
     * @param pageable  페이지 정보.
     * @return 페이지 별 매장 리스트
     */
    Page<StoreListResponseDto> lookupStoresPage(Long accountId, Pageable pageable);
}
