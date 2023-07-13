package store.cookshoong.www.cookshoongbackend.shop.repository.merchant;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllMerchantsForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectMerchantResponseDto;

/**
 * 가맹점 Custom 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@NoRepositoryBean
public interface MerchantRepositoryCustom {

    /**
     * 가맹점 리스트 페이지별 조회.
     *
     * @param pageable 페이지 정보
     * @return 각 페이지별 가맹점 리스트
     */
    Page<SelectMerchantResponseDto> lookupMerchantPage(Pageable pageable);

    List<SelectAllMerchantsForUserResponseDto> lookupMerchants();
}
