package store.cookshoong.www.cookshoongbackend.account.repository;

import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountResponseDto;

/**
 * 회원 정보(등급, 권한, 상태를 포함)를 가져오는 Repository.
 *
 * @author koesnam (추만석)
 * @since 2023.07.08
 */
@NoRepositoryBean
public interface AccountRepositoryCustom {

    /**
     * 회원에 대한 모든 정보를 가져오는 메서드.
     *
     * @param accountId accountId
     * @return 회원정보(옵셔널)
     */
    Optional<SelectAccountResponseDto> lookupAccount(Long accountId);
}
