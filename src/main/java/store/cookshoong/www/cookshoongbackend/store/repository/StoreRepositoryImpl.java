package store.cookshoong.www.cookshoongbackend.store.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.QAccount;
import store.cookshoong.www.cookshoongbackend.address.entity.QAddress;
import store.cookshoong.www.cookshoongbackend.store.entity.QBankType;
import store.cookshoong.www.cookshoongbackend.store.entity.QStore;
import store.cookshoong.www.cookshoongbackend.store.entity.QStoreStatus;
import store.cookshoong.www.cookshoongbackend.store.model.response.StoreListSearchResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.StoreSearchResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.UserStoreSearchResponseDto;

/**
 * 매장 커스텀 레포지토리 구현.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<StoreListSearchResponseDto> lookupStoresPage(Long accountId, Pageable pageable) {
        List<StoreListSearchResponseDto> responseDtoList = getStores(accountId, pageable);
        long total = getTotal(accountId);
        return new PageImpl<>(responseDtoList, pageable, total);
    }

    /**
     * 사업자 회원의 가게 리스트.
     *
     * @param accountId 회원 인덱스 번호
     * @param pageable  페이지 정보
     * @return 각 페이지에 해당하는 매장 리스트
     */
    private List<StoreListSearchResponseDto> getStores(Long accountId, Pageable pageable) {
        QStore store = QStore.store;
        QAccount account = QAccount.account;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;
        QAddress address = QAddress.address;

        return jpaQueryFactory
            .select(Projections.constructor(StoreListSearchResponseDto.class,
                store.id, account.loginId, store.name,
                address.mainPlace, address.detailPlace, storeStatus.description))
            .from(store)
            .innerJoin(store.account, account)
            .innerJoin(store.storeStatusCode, storeStatus)
            .innerJoin(store.address, address)
            .where(store.account.id.eq(accountId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    /**
     * 회원이 가지고 있는 매장 리스트의 총 개수.
     *
     * @param accountId 회원 아이디
     * @return 회원이 가지고 있는 매장의 총 개수
     */
    private Long getTotal(Long accountId) {
        QStore store = QStore.store;
        QAccount account = QAccount.account;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;
        QAddress address = QAddress.address;

        return jpaQueryFactory
            .select(store.count())
            .from(store)
            .innerJoin(store.account, account)
            .innerJoin(store.storeStatusCode, storeStatus)
            .innerJoin(store.address, address)
            .where(store.account.id.eq(accountId))
            .fetchOne();
    }

}
