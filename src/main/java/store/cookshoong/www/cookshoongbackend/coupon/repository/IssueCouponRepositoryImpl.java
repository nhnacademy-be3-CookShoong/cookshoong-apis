package store.cookshoong.www.cookshoongbackend.coupon.repository;

import static store.cookshoong.www.cookshoongbackend.account.entity.QAccount.account;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponLog.couponLog;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponLogType.couponLogType;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponPolicy.couponPolicy;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponType.couponType;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponUsage.couponUsage;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QIssueCoupon.issueCoupon;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    private final JPAQueryFactory queryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CouponResponseTempDto> lookupAllOwnCoupons(Long accountId) {
        return queryFactory
            .select(new QCouponResponseTempDto(
                issueCoupon.code,
                couponType,
                couponUsage,
                couponPolicy.name,
                couponPolicy.description,
                couponPolicy.expirationTime,
                couponLogType.name))
            .from(issueCoupon)

            .innerJoin(issueCoupon.couponPolicy, couponPolicy)
            .innerJoin(couponPolicy.couponType, couponType)
            .innerJoin(couponPolicy.couponUsage, couponUsage)
            .innerJoin(issueCoupon.account, account)

            .leftJoin(issueCoupon, couponLog.issueCoupon)
            .on(couponLog.id.eq(JPAExpressions
                .select(couponLog.id.max())
                .from(couponLog)
                .where(couponLog.issueCoupon.eq(issueCoupon))))
            .leftJoin(couponLog.couponLogType, couponLogType)

            .where(account.id.eq(accountId))
            .fetch();
    }

}
