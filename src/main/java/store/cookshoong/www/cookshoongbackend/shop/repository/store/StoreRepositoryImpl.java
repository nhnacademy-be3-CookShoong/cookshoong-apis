package store.cookshoong.www.cookshoongbackend.shop.repository.store;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.address.entity.QAddress;
import store.cookshoong.www.cookshoongbackend.file.entity.QImage;
import store.cookshoong.www.cookshoongbackend.shop.entity.QBankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStoresHasCategory;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectStoreCategoriesDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreCategoriesDto;
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
     * 사업자 회원의 가게 리스트.
     *
     * @param accountId 회원 인덱스 번호
     * @return 각 페이지에 해당하는 매장 리스트
     */
    @Override
    public List<SelectAllStoresResponseDto> lookupStores(Long accountId) {
        QStore store = QStore.store;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;
        QAddress address = QAddress.address;

        return jpaQueryFactory
            .select(new QSelectAllStoresResponseDto(
                store.id, store.name,
                address.mainPlace, address.detailPlace, storeStatus.code))
            .from(store)
            .innerJoin(store.storeStatus, storeStatus)
            .innerJoin(store.address, address)
            .where(store.account.id.eq(accountId))
            .orderBy(store.id.asc())
            .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SelectStoreResponseDto> lookupStore(Long accountId, Long storeId) {
        QStore store = QStore.store;
        QAddress address = QAddress.address;
        QBankType bankType = QBankType.bankType;
        QImage image = QImage.image;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;

        return Optional.ofNullable(jpaQueryFactory
            .select(new QSelectStoreResponseDto(
                store.businessLicenseNumber,
                store.representativeName,
                store.openingDate,
                store.name, store.phoneNumber,
                address.mainPlace, address.detailPlace, address.latitude, address.longitude, store.defaultEarningRate,
                store.minimumOrderPrice, store.description, bankType.description, store.bankAccountNumber,
                store.storeImage.savedName, store.storeImage.locationType, store.storeImage.domainName, store.deliveryCost, store.storeStatus.description))
            .from(store)
            .innerJoin(store.address, address)
            .innerJoin(store.bankTypeCode, bankType)
            .innerJoin(store.storeImage, image)
            .innerJoin(store.storeStatus, storeStatus)
            .where(store.id.eq(storeId))
            .fetchOne());
    }

    @Override
    public List<SelectStoreCategoriesDto> lookupStoreCategories(Long storeId) {
        QStore store = QStore.store;
        QStoresHasCategory storesHasCategory = QStoresHasCategory.storesHasCategory;
        QStoreCategory storeCategory = QStoreCategory.storeCategory;

        return jpaQueryFactory
            .select(new QSelectStoreCategoriesDto(storesHasCategory.categoryCode.description))
            .from(storesHasCategory)
            .innerJoin(storesHasCategory.store, store)
            .innerJoin(storesHasCategory.categoryCode, storeCategory)
            .where(storesHasCategory.pk.storeId.eq(storeId))
            .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SelectStoreForUserResponseDto> lookupStoreForUser(Long storeId) {
        QStore store = QStore.store;
        QAddress address = QAddress.address;
        QImage image = QImage.image;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;

        return Optional.ofNullable(jpaQueryFactory
            .select(new QSelectStoreForUserResponseDto(
                store.businessLicenseNumber, store.representativeName, store.openingDate, store.name,
                store.phoneNumber, address.mainPlace, address.detailPlace, store.description,
                store.storeImage.locationType, store.storeImage.domainName, store.storeImage.savedName, store.minimumOrderPrice, store.deliveryCost, storeStatus.description))
            .from(store)
            .innerJoin(store.address, address)
            .innerJoin(store.storeImage, image)
            .innerJoin(store.storeStatus, storeStatus)
            .where(store.id.eq(storeId))
            .fetchOne());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectAllStoresNotOutedResponseDto> lookupStoreLatLanPage(Pageable pageable) {
        List<SelectAllStoresNotOutedResponseDto> responseDtos = lookupNotOutedStore(pageable);
        Long total = lookupNotOutedStoreTotal();
        return new PageImpl<>(responseDtos, pageable, total);
    }

    private List<SelectAllStoresNotOutedResponseDto> lookupNotOutedStore(Pageable pageable) {
        QStore store = QStore.store;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;
        QStoresHasCategory storesHasCategory = QStoresHasCategory.storesHasCategory;
        QStoreCategory storeCategory = QStoreCategory.storeCategory;
        QAddress address = QAddress.address;
        QImage image = QImage.image;

        return jpaQueryFactory
            .select(new QSelectAllStoresNotOutedResponseDto(
                store.id, store.name, storeStatus.description, address.mainPlace,
                address.detailPlace, address.latitude, address.longitude, storeCategory.categoryCode, store.storeImage.savedName,
                store.storeImage.locationType, store.storeImage.domainName, store.minimumOrderPrice, store.deliveryCost))
            .from(store)
            .innerJoin(store.storeStatus, storeStatus)
            .innerJoin(store.address, address)
            .innerJoin(store.storesHasCategories, storesHasCategory)
            .innerJoin(storesHasCategory.categoryCode, storeCategory)
            .leftJoin(store.storeImage, image)
            .where(storeStatus.code.ne("OUTED"))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private Long lookupNotOutedStoreTotal() {
        QStore store = QStore.store;
        QStoreStatus storeStatus = QStoreStatus.storeStatus;
        QAddress address = QAddress.address;
        QImage image = QImage.image;

        return jpaQueryFactory
            .select(store.count())
            .from(store)
            .innerJoin(store.storeStatus, storeStatus)
            .innerJoin(store.address, address)
            .innerJoin(store.storeImage, image)
            .where(storeStatus.code.ne("OUTED"))
            .fetchOne();
    }
}
