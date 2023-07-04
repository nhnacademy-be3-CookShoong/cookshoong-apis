package store.cookshoong.www.cookshoongbackend.address.repository.accountaddress;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;
import store.cookshoong.www.cookshoongbackend.address.entity.QAccountAddress;
import store.cookshoong.www.cookshoongbackend.address.entity.QAddress;
import store.cookshoong.www.cookshoongbackend.address.model.response.MainAddressResponseDto;

/**
 * 회원과 주소 repository.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
public class AccountAddressRepositoryImpl implements AccountAddressRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public AccountAddressRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * 회원의 주소 리스트를 조회 합니다.
     *
     * @param accountId 회원 아이디
     * @return 회원 주소 목록
     */
    @Override
    public List<MainAddressResponseDto> findAccountByAccountId(Long accountId) {
        QAccountAddress accountAddress = QAccountAddress.accountAddress;
        QAddress address = QAddress.address;

        return jpaQueryFactory
            .select(Projections.constructor(MainAddressResponseDto.class, address.mainPlace))
            .from(accountAddress)
            .join(accountAddress.address, address)
            .where(accountAddress.pk.addressId.eq(accountId))
            .fetch();
    }
}
