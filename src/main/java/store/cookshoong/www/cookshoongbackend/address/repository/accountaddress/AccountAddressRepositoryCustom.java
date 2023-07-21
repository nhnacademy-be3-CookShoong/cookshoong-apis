package store.cookshoong.www.cookshoongbackend.address.repository.accountaddress;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.address.model.response.AccountAddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;

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
     * @param accountId     회원 아이디
     * @return              회원 주소 목록
     */
    List<AccountAddressResponseDto> lookupByAccountIdAddressAll(Long accountId);

    /**
     * 회원이 가지고 있는 주소 중 최근의 갱신된 주소 조회.
     *
     * @param accountId     회원 아이디
     * @return              회원이 최근의 등록한 주소와 좌표를 반환
     */
    AddressResponseDto lookupByAccountAddressRenewalAt(Long accountId);

    /**
     * 회원이 주소록 중 선택한 주소와 좌표를 가져옵니다.
     *
     * @param addressId     주소 아이디
     * @return              회원이 최근의 등록한 주소와 좌표를 반환
     */
    AddressResponseDto lookupByAccountSelectAddressId(Long addressId);
}
