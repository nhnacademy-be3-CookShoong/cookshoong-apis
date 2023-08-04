package store.cookshoong.www.cookshoongbackend.coupon.service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponOverCountException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponRedisRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.coupon.util.IssueMethod;

/**
 * 쿠폰 발행 서비스.
 *
 * @author eora21 (김주호)
 * @since 2023.07.17
 */
@Service
@Transactional
@RequiredArgsConstructor
public class IssueCouponService {
    private static final int LIMIT_COUNT = 1_000;

    private final Map<IssueMethod, BiConsumer<CouponPolicy, Long>> issueMethodConsumer = createIssueConsumer();

    private final IssueCouponRepository issueCouponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final CouponRedisRepository couponRedisRepository;

    private Map<IssueMethod, BiConsumer<CouponPolicy, Long>> createIssueConsumer() {
        Map<IssueMethod, BiConsumer<CouponPolicy, Long>> enumMap = new EnumMap<>(IssueMethod.class);
        enumMap.put(IssueMethod.BULK, this::createIssueCouponAllAccounts);
        enumMap.put(IssueMethod.EVENT, this::createIssueCouponFirstComeFirstServe);
        enumMap.put(IssueMethod.NORMAL, this::createIssueCouponInLimitCount);

        return enumMap;
    }

    /**
     * 쿠폰 발행 메서드.
     * 요청된 개수만큼 발행시킨다.
     *
     * @param createIssueCouponRequestDto the issue coupon request dto
     */
    public void createIssueCoupon(CreateIssueCouponRequestDto createIssueCouponRequestDto) {
        CouponPolicy couponPolicy = couponPolicyRepository.findById(createIssueCouponRequestDto.getCouponPolicyId())
            .orElseThrow(CouponPolicyNotFoundException::new);

        IssueMethod issueMethod = createIssueCouponRequestDto.getIssueMethod();

        couponPolicy.getCouponUsage()
            .validIssueMethod(issueMethod);

        issueMethodConsumer.get(issueMethod)
            .accept(couponPolicy, createIssueCouponRequestDto.getIssueQuantity());
    }

    private void createIssueCouponAllAccounts(CouponPolicy couponPolicy, Long issueQuantity) {
        // TODO: Spring Batch 적용 이후 이용해볼 것
    }

    private void createIssueCouponFirstComeFirstServe(CouponPolicy couponPolicy, Long issueQuantity) {
        Set<IssueCoupon> issueCoupons = Stream.generate(() -> new IssueCoupon(couponPolicy))
            .limit(issueQuantity)
            .collect(Collectors.toSet());
        issueCouponRepository.saveAllAndFlush(issueCoupons);

        Set<UUID> couponCodes = issueCoupons.stream()
            .map(IssueCoupon::getCode)
            .collect(Collectors.toSet());

        couponRedisRepository.bulkInsertCouponCode(couponCodes, String.valueOf(couponPolicy.getId()));
    }

    private void createIssueCouponInLimitCount(CouponPolicy couponPolicy, Long issueQuantity) {
        checkUnclaimedCouponCount(couponPolicy.getId(), issueQuantity);

        Stream.generate(() -> new IssueCoupon(couponPolicy))
            .limit(issueQuantity)
            .forEach(issueCouponRepository::save);
    }

    /**
     * 유저가 아직 수령하지 않은 유효 쿠폰 개수 + 발행 요청 개수가 limitCount 초과하는지 확인.
     *
     * @param couponPolicyId 쿠폰 정책 id
     * @param issueQuantity  발행 요청 개수
     */
    private void checkUnclaimedCouponCount(Long couponPolicyId, Long issueQuantity) {
        Long unclaimedCouponCount = couponPolicyRepository.lookupUnclaimedCouponCount(couponPolicyId);

        if (LIMIT_COUNT < unclaimedCouponCount + issueQuantity) {
            throw new IssueCouponOverCountException(LIMIT_COUNT);
        }
    }

    /**
     * 쿠폰 코드로 발행된 쿠폰을 찾는 메서드.
     *
     * @param issueCouponCode the issue coupon code
     * @return the issue coupon
     */
    public IssueCoupon selectIssueCouponByCode(UUID issueCouponCode) {
        return issueCouponRepository.findById(issueCouponCode)
            .orElseThrow(IssueCouponNotFoundException::new);
    }
}
