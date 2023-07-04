package store.cookshoong.www.cookshoongbackend.address.repository.accountaddress;

import com.querydsl.core.types.Projections;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import store.cookshoong.www.cookshoongbackend.address.dto.response.MainAddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.entity.AccountAddress;
import store.cookshoong.www.cookshoongbackend.address.entity.QAccountAddress;
import store.cookshoong.www.cookshoongbackend.address.entity.QAddress;

/**
 * 회원과 주소 repository.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
public class AccountAddressImpl extends QuerydslRepositorySupport implements AccountAddressRepositoryCustom {

    public AccountAddressImpl() {
        super(AccountAddress.class);
    }

    /**
     * 회원의 주소 리스를 조회 합니다.
     *
     * @param accountId 회원 아이디
     * @return 회원 주소 목록
     */
    @Override
    public List<MainAddressResponseDto> findAccountByAccountId(Long accountId) {
        QAccountAddress accountAddress = QAccountAddress.accountAddress;
        QAddress address = QAddress.address;

        return from(accountAddress)
            .select(Projections.constructor(MainAddressResponseDto.class, address.mainPlace))
            .join(accountAddress.address, address)
            .where(accountAddress.pk.addressId.eq(accountId))
            .fetch();
    }
}
