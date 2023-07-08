package store.cookshoong.www.cookshoongbackend.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.store.entity.QMerchant;
import store.cookshoong.www.cookshoongbackend.store.model.response.QSelectMerchantResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectMerchantResponseDto;

/**
 * 가맹점 Custom Repository.
 * 가맹점 리스트 pagination 조회.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@RequiredArgsConstructor
public class MerchantRepositoryImpl implements MerchantRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectMerchantResponseDto> lookupMerchantPage(Pageable pageable) {
        List<SelectMerchantResponseDto> responseDtoList = lookupMerchants(pageable);
        long total = lookupTotal();
        return new PageImpl<>(responseDtoList, pageable, total);
    }

    private List<SelectMerchantResponseDto> lookupMerchants(Pageable pageable) {
        QMerchant merchant = QMerchant.merchant;
        return jpaQueryFactory
            .select(new QSelectMerchantResponseDto(merchant.name))
            .from(merchant)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private Long lookupTotal() {
        QMerchant merchant = QMerchant.merchant;
        return jpaQueryFactory
            .select(merchant.count())
            .from(merchant)
            .fetchOne();
    }
}
