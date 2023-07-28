package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.QOption;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.QOptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.QSelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;

/**
 * 옵션 커스텀 레포지토리 구현.
 *
 * @author papel (윤동현)
 * @since 2023.07.17
 */
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 메뉴 옵션 조회.
     *
     * @param optionId 옵션 아이디
     * @return 매장의 옵션
     */
    @Override
    public Optional<SelectOptionResponseDto> lookupOption(Long optionId) {
        QOption option = QOption.option;
        QOptionGroup optionGroup = QOptionGroup.optionGroup;
        QStore store = QStore.store;

        return Optional.ofNullable(jpaQueryFactory
            .select(new QSelectOptionResponseDto(
                option.id, optionGroup.id, option.name,
                option.price, option.isDeleted,
                option.optionSequence))
            .from(option)
            .innerJoin(option.optionGroup, optionGroup)
            .innerJoin(optionGroup.store, store)
            .where(option.id.eq(optionId))
            .fetchOne());
    }

    /**
     * 메뉴 옵션 리스트 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장의 옵션 리스트
     */
    @Override
    public List<SelectOptionResponseDto> lookupOptions(Long storeId) {
        QOption option = QOption.option;
        QOptionGroup optionGroup = QOptionGroup.optionGroup;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(new QSelectOptionResponseDto(
                option.id, optionGroup.id, option.name,
                option.price, option.isDeleted,
                option.optionSequence))
            .from(option)
            .innerJoin(option.optionGroup, optionGroup)
            .innerJoin(optionGroup.store, store)
            .where(store.id.eq(storeId), option.isDeleted.isFalse())
            .fetch();
    }
}
