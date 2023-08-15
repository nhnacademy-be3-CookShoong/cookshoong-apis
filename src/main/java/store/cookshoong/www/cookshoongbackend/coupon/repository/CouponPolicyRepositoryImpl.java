package store.cookshoong.www.cookshoongbackend.coupon.repository;

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
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.QSelectPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.QSelectProvableCouponPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectProvableCouponPolicyResponseDto;

/**
 * QueryDSL CouponPolicyRepository.
 * 쿠폰 정책을 조회할 수 있도록 한다.
 *
 * @author eora21(김주호)
 * @since 2023.07.14
 */
@RequiredArgsConstructor
public class CouponPolicyRepositoryImpl implements CouponPolicyRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectPolicyResponseDto> lookupStorePolicy(Long storeId, Pageable pageable) {
        return getCouponPolicyPage(getCouponUsageStoreId(storeId), pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectPolicyResponseDto> lookupMerchantPolicy(Long merchantId, Pageable pageable) {
        return getCouponPolicyPage(getCouponUsageMerchantId(merchantId), pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long lookupUnclaimedCouponCount(Long couponPolicyId) {
        return queryFactory
            .select(getIssueCouponCount(true))
            .from(couponPolicy)
            .where(couponPolicy.id.eq(couponPolicyId))
            .fetchOne();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectPolicyResponseDto> lookupAllPolicy(Pageable pageable) {
        return getCouponPolicyPage(getCouponUsageAllId(), pageable);
    }

    private Page<SelectPolicyResponseDto> getCouponPolicyPage(JPQLQuery<Long> couponUsageId, Pageable pageable) {
        List<SelectPolicyResponseDto> couponPolicy = getCouponPolicy(couponUsageId, pageable);
        Long total = getTotal(couponUsageId);

        return new PageImpl<>(couponPolicy, pageable, total);
    }

    private List<SelectPolicyResponseDto> getCouponPolicy(JPQLQuery<Long> couponUsageId, Pageable pageable) {
        return queryFactory
            .select(new QSelectPolicyResponseDto(
                couponPolicy.id,
                couponType,
                couponPolicy.name,
                couponPolicy.description,
                couponPolicy.usagePeriod,
                getIssueCouponCount(true),
                getIssueCouponCount(false)
            ))
            .from(couponPolicy)

            .innerJoin(couponPolicy.couponType, couponType)
            .innerJoin(couponPolicy.couponUsage, couponUsage)
            .on(couponUsage.id.eq(couponUsageId))

            .where(couponPolicy.deleted.isFalse(), couponPolicy.hidden.isFalse())

            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private static JPQLQuery<Long> getCouponUsageStoreId(Long storeId) {
        return JPAExpressions.select(couponUsageStore.id)
            .from(couponUsageStore)
            .innerJoin(couponUsageStore.store, store)
            .where(store.id.eq(storeId));
    }

    private static JPQLQuery<Long> getCouponUsageMerchantId(Long merchantId) {
        return JPAExpressions.select(couponUsageMerchant.id)
            .from(couponUsageMerchant)
            .innerJoin(couponUsageMerchant.merchant, merchant)
            .where(merchant.id.eq(merchantId));
    }

    private static JPQLQuery<Long> getCouponUsageAllId() {
        return JPAExpressions.select(couponUsageAll.id.min())
            .from(couponUsageAll);
    }

    private Long getTotal(JPQLQuery<Long> couponUsageId) {
        return queryFactory
            .select(couponPolicy.count())
            .from(couponPolicy)

            .innerJoin(couponPolicy.couponType, couponType)
            .innerJoin(couponPolicy.couponUsage, couponUsage)

            .where(couponUsage.id.eq(couponUsageId), couponPolicy.deleted.isFalse())
            .fetchOne();
    }

    private JPQLQuery<Long> getIssueCouponCount(boolean receivable) {
        return JPAExpressions.select(issueCoupon.count())
            .from(issueCoupon)
            .where(issueCoupon.couponPolicy.eq(couponPolicy), isUnclaimedCouponCount(receivable));
    }

    private BooleanExpression isUnclaimedCouponCount(boolean receivable) {
        if (!receivable) {
            return null;
        }

        return issueCoupon.account.isNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SelectProvableCouponPolicyResponseDto> lookupProvableStoreCouponPolicies(Long storeId) {
        return getProvableCouponPolicies(couponUsage.id.eq(getCouponUsageStoreId(storeId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SelectProvableCouponPolicyResponseDto> lookupProvableMerchantCouponPolicies(Long merchantId) {
        return getProvableCouponPolicies(couponUsage.id.eq(getCouponUsageMerchantId(merchantId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SelectProvableCouponPolicyResponseDto> lookupProvableUsageAllCouponPolicies() {
        return getProvableCouponPolicies(couponUsage.id.eq(getCouponUsageAllId()));
    }

    private List<SelectProvableCouponPolicyResponseDto> getProvableCouponPolicies(BooleanExpression filter) {
        return queryFactory
            .select(new QSelectProvableCouponPolicyResponseDto(
                couponPolicy.id,
                couponType,
                couponPolicy.usagePeriod
            ))
            .from(couponPolicy)

            .innerJoin(couponPolicy.couponType, couponType)

            .innerJoin(couponPolicy.couponUsage, couponUsage)
            .on(filter)

            .where(couponPolicy.deleted.isFalse(), existReceivableIssueCoupon())
            .fetch();
    }

    private BooleanExpression existReceivableIssueCoupon() {
        return JPAExpressions
            .selectFrom(issueCoupon)
            .where(issueCoupon.couponPolicy.eq(couponPolicy), isUnclaimedCouponCount(true))
            .exists();
    }

    @Override
    public boolean isOfferCouponInStore(Long storeId) {
        Object uuid = queryFactory
            .selectFrom(couponUsageStore)

            .innerJoin(couponPolicy)
            .on(couponPolicy.couponUsage.id.eq(couponUsageStore.id))

            .innerJoin(issueCoupon)
            .on(issueCoupon.couponPolicy.id.eq(couponPolicy.id))

            .where(couponUsageStore.store.id.eq(storeId))
            .fetchFirst();

        return Objects.nonNull(uuid);
    }
}
