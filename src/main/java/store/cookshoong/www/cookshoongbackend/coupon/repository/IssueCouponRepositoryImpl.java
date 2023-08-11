package store.cookshoong.www.cookshoongbackend.coupon.repository;

import static store.cookshoong.www.cookshoongbackend.account.entity.QAccount.account;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponLog.couponLog;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponLogType.couponLogType;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponPolicy.couponPolicy;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponType.couponType;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponUsage.couponUsage;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponUsageAll.couponUsageAll;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponUsageMerchant.couponUsageMerchant;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponUsageStore.couponUsageStore;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QIssueCoupon.issueCoupon;
import static store.cookshoong.www.cookshoongbackend.shop.entity.QMerchant.merchant;
import static store.cookshoong.www.cookshoongbackend.shop.entity.QStore.store;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.QSelectOwnCouponResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectOwnCouponResponseDto;

/**
 * QueryDSL IssueCouponRepository.
 * 사용자 소유의 쿠폰을 조회할 수 있도록 한다.
 *
 * @author eora21
 * @since 2023.07.06
 */
@RequiredArgsConstructor
public class IssueCouponRepositoryImpl implements IssueCouponRepositoryCustom {
    private static final String COUPON_LOG_TYPE_CODE_USE = "USE";
    private final JPAQueryFactory queryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectOwnCouponResponseDto> lookupAllOwnCoupons(Long accountId, Pageable pageable, Boolean useCond,
                                                                Long storeId) {
        List<SelectOwnCouponResponseDto> content = getCouponResponses(accountId, pageable, useCond, storeId);
        Long total = getTotal(accountId, useCond, storeId);
        return new PageImpl<>(content, pageable, total);
    }

    private List<SelectOwnCouponResponseDto> getCouponResponses(Long accountId, Pageable pageable,
                                                                Boolean usable, Long storeId) {

        return queryFactory
            .select(new QSelectOwnCouponResponseDto(
                issueCoupon.code,
                couponType,
                couponUsage,
                couponPolicy.name,
                couponPolicy.description,
                issueCoupon.expirationDate,
                couponLogType.description))

            .from(issueCoupon)

            .innerJoin(issueCoupon.couponPolicy, couponPolicy)
            .innerJoin(couponPolicy.couponType, couponType)
            .innerJoin(couponPolicy.couponUsage, couponUsage)
            .innerJoin(issueCoupon.account, account)

            .leftJoin(couponLog)
            .on(couponLog.id.eq(getMaxCouponLogId()))

            .leftJoin(couponLog.couponLogType, couponLogType)

            .where(account.id.eq(accountId),
                checkUsableCoupon(usable),
                checkCouponUsage(storeId))

            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private static JPQLQuery<Long> getMaxCouponLogId() {
        return JPAExpressions
            .select(couponLog.id.max())
            .from(couponLog)
            .where(couponLog.issueCoupon.eq(issueCoupon));
    }

    private static BooleanExpression checkUsableCoupon(Boolean usable) {
        if (usable == null) {
            return null;
        }

        if (usable) {
            return couponLogType.code.ne(COUPON_LOG_TYPE_CODE_USE)
                .or(couponLogType.isNull()
                    .and(issueCoupon.expirationDate.goe(LocalDate.now())));
        }

        return couponLogType.code.eq(COUPON_LOG_TYPE_CODE_USE)
            .or(issueCoupon.expirationDate.lt(LocalDate.now()));
    }

    private static BooleanExpression checkCouponUsage(Long storeId) {
        if (storeId == null) {
            return null;
        }

        return couponUsage.id.in(getCouponUsageStoreId(storeId))
            .or(couponUsage.id.in(getCouponUsageMerchantId(storeId)))
            .or(couponUsage.id.in(getCouponUsageAllId()));
    }

    private static JPQLQuery<Long> getCouponUsageStoreId(Long storeId) {
        return JPAExpressions
            .select(couponUsageStore.id)
            .from(couponUsageStore)
            .innerJoin(couponUsageStore.store, store)
            .on(store.id.eq(storeId));
    }

    private static JPQLQuery<Long> getCouponUsageMerchantId(Long storeId) {
        return JPAExpressions
            .select(couponUsageMerchant.id)
            .from(couponUsageMerchant)
            .innerJoin(couponUsageMerchant.merchant, merchant)
            .on(merchant.id.eq(getMerchantId(storeId)));
    }

    private static JPQLQuery<Long> getMerchantId(Long storeId) {
        return JPAExpressions
            .select(merchant.id)
            .from(store)
            .innerJoin(store.merchant, merchant)
            .where(store.id.eq(storeId));
    }

    private static JPQLQuery<Long> getCouponUsageAllId() {
        return JPAExpressions
            .select(couponUsageAll.id)
            .from(couponUsageAll);
    }

    private Long getTotal(Long accountId, Boolean usable, Long storeId) {
        return queryFactory
            .select(issueCoupon.count())
            .from(issueCoupon)

            .innerJoin(issueCoupon.couponPolicy, couponPolicy)
            .innerJoin(couponPolicy.couponType, couponType)
            .innerJoin(couponPolicy.couponUsage, couponUsage)
            .innerJoin(issueCoupon.account, account)

            .leftJoin(couponLog)
            .on(couponLog.id.eq(getMaxCouponLogId()))

            .leftJoin(couponLog.couponLogType, couponLogType)

            .where(account.id.eq(accountId),
                checkUsableCoupon(usable),
                checkCouponUsage(storeId))
            .fetchOne();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void provideCouponToAccount(IssueCoupon issueCouponEntity, Account account) {
        LocalDate expirationDate = LocalDate.now().plusDays(issueCouponEntity.getCouponPolicy().getUsagePeriod());

        long updatedCount = queryFactory.update(issueCoupon)
            .set(issueCoupon.account, account)
            .set(issueCoupon.expirationDate, expirationDate)
            .set(issueCoupon.receiptDate, LocalDate.now())
            .where(issueCoupon.eq(issueCouponEntity), issueCoupon.account.isNull())
            .execute();

        if (updatedCount == 0) {
            throw new ProvideIssueCouponFailureException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReceivedBefore(Long couponPolicyId, Long accountId, Integer usagePeriod) {
        LocalDate today = LocalDate.now();
        LocalDate ago = LocalDate.now().minusDays(usagePeriod);

        return queryFactory
            .select(issueCoupon.code)
            .from(issueCoupon)

            .innerJoin(issueCoupon.couponPolicy, couponPolicy)
            .on(couponPolicy.id.eq(couponPolicyId))

            .leftJoin(couponLog)
            .on(couponLog.id.eq(getMaxCouponLogId()))

            .leftJoin(couponLog.couponLogType, couponLogType)

            .where(issueCoupon.account.id.eq(accountId), issueCoupon.receiptDate.between(ago, today))
            .fetchFirst() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<UUID> lookupUnclaimedCouponCodes() {
        return new HashSet<>(queryFactory
            .select(issueCoupon.code)
            .from(issueCoupon)
            .where(issueCoupon.account.isNull())
            .fetch());
    }
}
