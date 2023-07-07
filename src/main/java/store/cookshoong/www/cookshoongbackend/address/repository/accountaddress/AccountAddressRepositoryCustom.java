package store.cookshoong.www.cookshoongbackend.address.repository.accountaddress;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.address.model.response.AccountAddressResponseDto;

/**
 * 회원과 주소 repository.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@NoRepositoryBean
public interface AccountAddressRepositoryCustom {


    /**
     * 회원의 주소 리스트를 조회 합니다.
     *
     * @param accountId 회원 아이디
     * @return 회원 주소 목록
     */
    List<AccountAddressResponseDto> getByAccountIdAddress(Long accountId);
}
