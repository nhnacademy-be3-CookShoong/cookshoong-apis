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
import static store.cookshoong.www.cookshoongbackend.store.entity.QMerchant.merchant;
import static store.cookshoong.www.cookshoongbackend.store.entity.QStore.store;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.coupon.model.temp.CouponResponseTempDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.temp.QCouponResponseTempDto;

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
    public Page<CouponResponseTempDto> lookupAllOwnCoupons(Long accountId, Pageable pageable, Boolean useCond,
                                                           Long storeId) {
        List<CouponResponseTempDto> content = getCouponResponseTemps(accountId, pageable, useCond, storeId);
        Long total = getTotal(accountId, useCond, storeId);
        return new PageImpl<>(content, pageable, total);

    }

    private List<CouponResponseTempDto> getCouponResponseTemps(Long accountId, Pageable pageable, Boolean usable,
                                                               Long storeId) {
        JPQLQuery<String> couponLogTypeDescription = getCouponLogTypeDescription();

        return queryFactory
            .select(new QCouponResponseTempDto(
                issueCoupon.code,
                couponType,
                couponUsage,
                couponPolicy.name,
                couponPolicy.description,
                couponPolicy.expirationTime,
                couponLogTypeDescription))

            .from(issueCoupon)

            .innerJoin(issueCoupon.couponPolicy, couponPolicy)
            .innerJoin(couponPolicy.couponType, couponType)
            .innerJoin(couponPolicy.couponUsage, couponUsage)
            .innerJoin(issueCoupon.account, account)

            .where(account.id.eq(accountId),
                couponLogTypeEq(couponLogTypeDescription, usable),
                couponUsageEq(storeId))

            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private static JPQLQuery<String> getCouponLogTypeDescription() {
        return JPAExpressions
            .select(couponLogType.description)
            .from(couponLog)
            .innerJoin(couponLog.couponLogType, couponLogType)
            .where(couponLog.id.eq(getMaxCouponLogId()));
    }

    private static JPQLQuery<Long> getMaxCouponLogId() {
        return JPAExpressions
            .select(couponLog.id.max())
            .from(couponLog)
            .where(couponLog.issueCoupon.eq(issueCoupon));
    }

    private static BooleanExpression couponLogTypeEq(JPQLQuery<String> couponLogTypeDescription, Boolean usable) {
        if (usable == null) {
            return null;
        }

        if (usable) {
            return couponLogTypeDescription.ne(getCouponLogTypeUseDescription())
                .or(couponLogTypeDescription.isNull());
        }

        return couponLogTypeDescription.eq(getCouponLogTypeUseDescription());
    }

    private static JPQLQuery<String> getCouponLogTypeUseDescription() {
        return JPAExpressions
            .select(couponLogType.description)
            .from(couponLogType)
            .where(couponLogType.code.eq(COUPON_LOG_TYPE_CODE_USE));
    }

    private static BooleanExpression couponUsageEq(Long storeId) {
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

            .where(account.id.eq(accountId),
                couponLogTypeEq(getCouponLogTypeDescription(), usable),
                couponUsageEq(storeId))
            .fetchOne();
    }

}
