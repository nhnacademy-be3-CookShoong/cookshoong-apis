package store.cookshoong.www.cookshoongbackend.shop.repository.stauts;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectAllStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStatusResponseDto;

/**
 * 매장 상 커스텀 레포지토리 구현.
 *
 * @author seungyeon
 * @since 2023.07.16
 */
@RequiredArgsConstructor
public class StoreStatusRepositoryImpl implements StoreStatusRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<SelectAllStatusResponseDto> lookupStatusForUser() {
        QStoreStatus storeStatus = QStoreStatus.storeStatus;

        return jpaQueryFactory
            .select(new QSelectAllStatusResponseDto(storeStatus.storeStatusCode, storeStatus.description))
            .from(storeStatus)
            .fetch();
    }
}
