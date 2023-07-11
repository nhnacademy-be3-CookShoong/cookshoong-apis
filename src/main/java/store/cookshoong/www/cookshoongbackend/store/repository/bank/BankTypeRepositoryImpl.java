package store.cookshoong.www.cookshoongbackend.store.repository.bank;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.store.entity.QBankType;
import store.cookshoong.www.cookshoongbackend.store.model.response.QSelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllBanksResponseDto;

/**
 * 은행 타입 레포지토리 작성.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@RequiredArgsConstructor
public class BankTypeRepositoryImpl implements BankTypeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectAllBanksResponseDto> lookupBanksPage(Pageable pageable) {
        List<SelectAllBanksResponseDto> responseDtos = lookupBanks(pageable);
        Long total = lookupTotal();
        return new PageImpl<>(responseDtos, pageable, total);
    }

    private List<SelectAllBanksResponseDto> lookupBanks(Pageable pageable) {
        QBankType bankType = QBankType.bankType;

        return jpaQueryFactory
            .select(new QSelectAllBanksResponseDto(bankType.bankTypeCode, bankType.description))
            .from(bankType)
            .orderBy(bankType.description.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private Long lookupTotal() {
        QBankType bankType = QBankType.bankType;
        return jpaQueryFactory
            .select(bankType.count())
            .from(bankType)
            .fetchOne();
    }

}
