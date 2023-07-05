package store.cookshoong.www.cookshoongbackend.address.repository.accountaddress;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.address.entity.AccountAddress;

/**
 * 회원과 주소 repository.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
public interface AccountAddressRepository extends JpaRepository<AccountAddress, AccountAddress.Pk>,
                                                                        AccountAddressRepositoryCustom{

}
