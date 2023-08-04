package store.cookshoong.www.cookshoongbackend.account.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.account.entity.QAccount;
import store.cookshoong.www.cookshoongbackend.account.entity.QAccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.QAuthority;
import store.cookshoong.www.cookshoongbackend.account.entity.QOauthAccount;
import store.cookshoong.www.cookshoongbackend.account.entity.QOauthType;
import store.cookshoong.www.cookshoongbackend.account.entity.QRank;
import store.cookshoong.www.cookshoongbackend.account.model.response.QSelectAccountInfoResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.QSelectAccountResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountInfoResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountResponseDto;

/**
 * 회원 정보(등급, 권한, 상태를 포함)를 가져오는 Repository 구현.
 *
 * @author koesnam (추만석)
 * @since 2023.07.08
 */
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SelectAccountResponseDto> lookupAccount(Long accountId) {
        QAccount account = QAccount.account;
        QAccountStatus status = QAccountStatus.accountStatus;
        QAuthority authority = QAuthority.authority;
        QRank rank = QRank.rank;

        return Optional.ofNullable(jpaQueryFactory.select(new QSelectAccountResponseDto(
                account.id, status.description, authority.description,
                rank.name, account.loginId, account.name,
                account.nickname, account.email, account.birthday,
                account.phoneNumber, account.lastLoginAt))
            .from(account)
            .innerJoin(account.status, status)
            .innerJoin(account.authority, authority)
            .innerJoin(account.rank, rank)
            .where(account.id.eq(accountId))
            .fetchFirst());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SelectAccountInfoResponseDto> lookupAccountInfoForOAuth(String provider, String accountCode) {
        QAccount account = QAccount.account;
        QOauthAccount oauthAccount = QOauthAccount.oauthAccount;
        QOauthType oauthType = QOauthType.oauthType;

        return Optional.ofNullable(jpaQueryFactory.select(new QSelectAccountInfoResponseDto(
                    account.id, account.loginId, account.authority.authorityCode, account.status.statusCode)
                ).from(oauthAccount)
                .innerJoin(oauthAccount.account, account)
                    .on(oauthAccount.accountCode.eq(accountCode))
                .innerJoin(oauthAccount.oauthType, oauthType)
                    .on(oauthType.provider.eq(provider))
                .fetchFirst()
        );
    }

}
