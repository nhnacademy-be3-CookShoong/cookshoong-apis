package store.cookshoong.www.cookshoongbackend.address.repository.accountaddress;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.address.dto.response.MainAddressResponseDto;

/**
 * {설명을 작성해주세요}.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */

@NoRepositoryBean
public interface AccountAddressRepositoryCustom {

    List<MainAddressResponseDto> findAccountByAccountId(Long accountId);
}
