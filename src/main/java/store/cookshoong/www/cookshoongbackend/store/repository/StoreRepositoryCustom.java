package store.cookshoong.www.cookshoongbackend.store.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.store.model.request.StoreRegisterRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.StoreListResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.StoreRegisterResponseDto;

/**
 * 매장 Custom 레포지토리 인터페이스.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@NoRepositoryBean
public interface StoreRepositoryCustom {
    Page<StoreListResponseDto> getStoresPage(Long accountId, Pageable pageable);
}
