package store.cookshoong.www.cookshoongbackend.store.repository.bank;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllBanksResponseDto;

/**
 * 은행 레포지토리 Custom.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@NoRepositoryBean
public interface BankTypeRepositoryCustom {
    /**
     * 은행 리스트 조회 pagination.
     *
     * @return 페이지 별 은행이름
     */
    Page<SelectAllBanksResponseDto> lookupBanksPage(Pageable pageable);
}
