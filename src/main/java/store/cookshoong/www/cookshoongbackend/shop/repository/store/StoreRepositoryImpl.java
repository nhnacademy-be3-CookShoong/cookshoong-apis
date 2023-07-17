package store.cookshoong.www.cookshoongbackend.shop.repository.store;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.QAccount;
import store.cookshoong.www.cookshoongbackend.address.entity.QAddress;
import store.cookshoong.www.cookshoongbackend.shop.entity.QBankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStoresHasCategory;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;

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
    public Page<SelectAllStoresResponseDto> lookupStoresPage(Long accountId, Pageable pageable) {
        List<SelectAllStoresResponseDto> responseDtos = lookupStores(accountId, pageable);
        long total = lookupTotal(accountId);
        return new PageImpl<>(responseDtos, pageable, total);
    }

    /**
     * 사업자 회원의 가게 리스트.
     *
     * @param accountId 회원 인덱스 번호
     * @param pageable  페이지 정보
     * @return 각 페이지에 해당하는 매장 리스트
     */
    private List<SelectAllStoresResponseDto> lookupStores(Long accountId, Pageable pageable) {
        QStore store = QStore.store;
        QAccount account = QAccount.account;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;
        QAddress address = QAddress.address;

        return jpaQueryFactory
            .select(new QSelectAllStoresResponseDto(
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
    private Long lookupTotal(Long accountId) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SelectStoreResponseDto> lookupStore(Long accountId, Long storeId) {
        QAccount account = QAccount.account;
        QStore store = QStore.store;
        QAddress address = QAddress.address;
        QBankType bankType = QBankType.bankType;

        return Optional.ofNullable(jpaQueryFactory
            .select(new QSelectStoreResponseDto(
                account.loginId,
                store.businessLicenseNumber,
                store.representativeName,
                store.openingDate,
                store.name, store.phoneNumber,
                address.mainPlace, address.detailPlace, address.latitude, address.longitude, store.defaultEarningRate,
                store.description, bankType.description, store.bankAccountNumber))
            .from(store)
            .innerJoin(store.account, account)
            .innerJoin(store.address, address)
            .innerJoin(store.bankTypeCode, bankType)
            .where(store.id.eq(storeId))
            .fetchOne());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SelectStoreForUserResponseDto> lookupStoreForUser(Long storeId) {
        QStore store = QStore.store;
        QAddress address = QAddress.address;

        return Optional.ofNullable(jpaQueryFactory
            .select(new QSelectStoreForUserResponseDto(
                store.businessLicenseNumber, store.representativeName, store.openingDate, store.name,
                store.phoneNumber, address.mainPlace, address.detailPlace, store.description))
            .from(store)
            .innerJoin(store.address, address)
            .where(store.id.eq(storeId))
            .fetchOne());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectAllStoresNotOutedResponseDto> lookupStoreLatLanPage(String storeCategoryCode, Pageable pageable) {
        List<SelectAllStoresNotOutedResponseDto> responseDtos = lookupNotOutedStore(storeCategoryCode, pageable);
        Long total = lookupNotOutedStoreTotal();
        return new PageImpl<>(responseDtos, pageable, total);
    }

    private List<SelectAllStoresNotOutedResponseDto> lookupNotOutedStore(String storeCategoryCode, Pageable pageable) {
        QStore store = QStore.store;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;
        QStoresHasCategory storesHasCategory = QStoresHasCategory.storesHasCategory;
        QStoreCategory storeCategory = QStoreCategory.storeCategory;
        QAddress address = QAddress.address;

        return jpaQueryFactory
            .select(new QSelectAllStoresNotOutedResponseDto(
                store.id, store.name, storeStatus.description, address.mainPlace,
                address.detailPlace, address.latitude, address.longitude))
            .from(store)
            .innerJoin(store.storeStatusCode, storeStatus)
            .innerJoin(store.address, address)
            .innerJoin(store.storesHasCategories, storesHasCategory)
            .innerJoin(storesHasCategory.categoryCode, storeCategory)
            .where(storeStatus.storeStatusCode.ne("OUTED").and(storeCategory.categoryCode.eq(storeCategoryCode)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private Long lookupNotOutedStoreTotal() {
        QStore store = QStore.store;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;
        QAddress address = QAddress.address;

        return jpaQueryFactory
            .select(store.count())
            .from(store)
            .innerJoin(store.storeStatusCode, storeStatus)
            .innerJoin(store.address, address)
            .where(storeStatus.storeStatusCode.ne("OUTED"))
            .fetchOne();
    }
}
