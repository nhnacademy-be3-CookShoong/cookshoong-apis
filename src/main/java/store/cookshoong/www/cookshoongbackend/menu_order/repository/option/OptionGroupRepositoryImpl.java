package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.QOptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.QSelectOptionGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;

/**
 * 옵션 그룹 커스텀 레포지토리 구현.
 *
 * @author papel (윤동현)
 * @since 2023.07.17
 */
@RequiredArgsConstructor
public class OptionGroupRepositoryImpl implements OptionGroupRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 매장 옵션 그룹 조회.
     *
     * @param optionGroupId 옵션 그룹 아이디
     * @return 매장의 옵션 그룹
     */
    @Override
    public Optional<SelectOptionGroupResponseDto> lookupOptionGroup(Long optionGroupId) {
        QOptionGroup optionGroup = QOptionGroup.optionGroup;
        QStore store = QStore.store;

        return Optional.ofNullable(jpaQueryFactory
            .select(new QSelectOptionGroupResponseDto(
                optionGroup.id, store.id, optionGroup.name,
                optionGroup.minSelectCount, optionGroup.maxSelectCount,
                optionGroup.isDeleted))
            .from(optionGroup)
            .innerJoin(optionGroup.store, store)
            .where(optionGroup.id.eq(optionGroupId))
            .fetchOne());
    }

    /**
     * 매장 옵션 그룹 리스트 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장의 옵션 그룹 리스트
     */
    @Override
    public List<SelectOptionGroupResponseDto> lookupOptionGroups(Long storeId) {
        QOptionGroup optionGroup = QOptionGroup.optionGroup;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(new QSelectOptionGroupResponseDto(
                optionGroup.id, store.id, optionGroup.name,
                optionGroup.minSelectCount, optionGroup.maxSelectCount,
                optionGroup.isDeleted))
            .from(optionGroup)
            .innerJoin(optionGroup.store, store)
            .where(store.id.eq(storeId), optionGroup.isDeleted.isFalse())
            .fetch();
    }

    /**
     * 매장 옵션 그룹 삭제.
     *
     * @param storeId       매장 아이디
     * @param optionGroupId 옵션 그룹 아이디
     */
    @Override
    public void deleteOptionGroup(Long storeId, Long optionGroupId) {
        QOptionGroup optionGroup = QOptionGroup.optionGroup;

        jpaQueryFactory
            .update(optionGroup)
            .set(optionGroup.isDeleted, true)
            .where(optionGroup.id.eq(optionGroupId))
            .execute();
    }
}
