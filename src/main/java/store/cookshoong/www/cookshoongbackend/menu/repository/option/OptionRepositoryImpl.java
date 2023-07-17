package store.cookshoong.www.cookshoongbackend.menu.repository.option;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu.entity.option.QOption;
import store.cookshoong.www.cookshoongbackend.menu.entity.optiongroup.QOptionGroup;
import store.cookshoong.www.cookshoongbackend.menu.model.response.QSelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.menu.model.response.SelectOptionResponseDto;

/**
 * 옵션 Custom 레포지토리 구현.
 *
 * @author papel
 * @since 2023.07.17
 */
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SelectOptionResponseDto> lookupOptions(Long storeId) {
        QOption option = QOption.option;
        QOptionGroup optionGroup = QOptionGroup.optionGroup;

        return jpaQueryFactory
            .select(new QSelectOptionResponseDto(
                option.id, option.name, option.price, option.isDeleted,
                option.optionSequence, optionGroup.id))
            .from(option)
            .innerJoin(option.optionGroup, optionGroup)
            .where(optionGroup.store.id.eq(storeId))
            .fetch();
    }
}
