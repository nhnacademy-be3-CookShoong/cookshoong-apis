package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.netflix.discovery.converters.Auto;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountStatusRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AuthorityRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.RankRepository;
import store.cookshoong.www.cookshoongbackend.config.IntegrationTestBase;
import store.cookshoong.www.cookshoongbackend.coupon.controller.ProvideCouponController;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponExhaustionException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponRedisRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypeCashRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageAllRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageStoreRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.coupon.util.IssueMethod;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;
import store.cookshoong.www.cookshoongbackend.lock.LockProcessor;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.repository.bank.BankTypeRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.stauts.StoreStatusRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

class ProvideCouponServiceMultiThreadTest extends IntegrationTestBase {
    @Autowired
    ProvideCouponService provideCouponService;
    @Autowired
    IssueCouponRepository issueCouponRepository;
    @Autowired
    CouponPolicyRepository couponPolicyRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CouponTypeCashRepository couponTypeCashRepository;
    @Autowired
    CouponUsageAllRepository couponUsageAllRepository;
    @Autowired
    CouponUsageStoreRepository couponUsageStoreRepository;
    @Autowired
    AccountStatusRepository accountStatusRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    RankRepository rankRepository;
    @Autowired
    RedisTemplate<String, Object> couponRedisTemplate;
    @Autowired
    IssueCouponService issueCouponService;
    @Autowired
    CouponRedisRepository couponRedisRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    BankTypeRepository bankTypeRepository;
    @Autowired
    StoreStatusRepository storeStatusRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ProvideCouponController provideCouponController;
    @Autowired
    LockProcessor lockProcessor;

    TestEntity te = new TestEntity();

    private UpdateProvideCouponRequestDto createUpdateProvideCouponRequestDto(Long couponPolicyId, Long accountId) {
        UpdateProvideCouponRequestDto updateProvideCouponRequestDto =
                te.createUsingDeclared(UpdateProvideCouponRequestDto.class);
        ReflectionTestUtils.setField(updateProvideCouponRequestDto, "couponPolicyId", couponPolicyId);
        ReflectionTestUtils.setField(updateProvideCouponRequestDto, "accountId", accountId);

        return updateProvideCouponRequestDto;
    }

    @ParameterizedTest
    @MethodSource("normalParameters")
    @DisplayName("멀티 스레드 일반 쿠폰 발급 테스트")
    void multiThreadProvideNormalCouponTest(int issueCount, int requestCount) throws Exception {
        final Result result = new Result();

        CouponTypeCash couponTypeCash = couponTypeCashRepository.save(te.getCouponTypeCash_1000_10000());

        Account storeAccount = accountRepository.save(te.getAccount(
            accountStatusRepository.save(te.getAccountStatusActive()),
            authorityRepository.save(te.getAuthorityBusiness()),
            rankRepository.save(te.getRankLevelOne())));

        Image image = imageRepository.save(te.getImage("locationType", "domainName", "name", true));

        Store store = storeRepository.save(te.getStore(
            merchantRepository.save(te.getMerchant()),
            storeAccount,
            bankTypeRepository.save(te.getBankTypeKb()),
            storeStatusRepository.save(te.getStoreStatusOpen()), image, image));
        CouponUsageStore couponUsageStore = couponUsageStoreRepository.save(te.getCouponUsageStore(store));

        CouponPolicy couponPolicy = couponPolicyRepository.save(te.getCouponPolicy(couponTypeCash, couponUsageStore));

        CreateIssueCouponRequestDto createIssueCouponRequestDto =
            te.createUsingDeclared(CreateIssueCouponRequestDto.class);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "couponPolicyId", couponPolicy.getId());
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", (long) issueCount);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueMethod", IssueMethod.NORMAL);

        issueCouponService.createIssueCoupon(createIssueCouponRequestDto);

        AccountStatus accountStatus = accountStatusRepository.save(new AccountStatus("OK", "허용"));
        Authority authority = authorityRepository.save(new Authority("NML", "일반"));
        Rank rank = rankRepository.save(new Rank("L1", "1레벨"));

        CountDownLatch countDownLatch = new CountDownLatch(requestCount);

        Stream.generate(() -> accountRepository.save(
                new Account(accountStatus, authority, rank, "eora21", "pw",
                    "김주호", "어라?!", "eora21@naver.com", LocalDate.now(),
                    "01012345678")))
            .limit(requestCount)
            .map(account ->
                new Thread(new Provider(result, countDownLatch,
                    createUpdateProvideCouponRequestDto(couponPolicy.getId(), account.getId()),
                    dto -> lockProcessor.lock(couponPolicy.getId().toString(), ignore ->
                        provideCouponService.provideCouponToAccountByApi(dto)))))
            .forEach(Thread::start);

        countDownLatch.await();

        int receiveCount = Math.min(issueCount, requestCount);
        int failCount = Math.max(requestCount - issueCount, 0);
        int leftCount = Math.max(issueCount - requestCount, 0);

        Long unclaimedCouponCount = couponPolicyRepository.lookupUnclaimedCouponCount(couponPolicy.getId());

        System.out.println("receive:" + result.getReceiveCount());
        System.out.println("fail:" + result.getTotalFailCount());
        System.out.println("exhaustion:" + result.getExhaustionCount());
        System.out.println("provideFail:" + result.getProvideFailCount());
        System.out.println("unclaimed:" + unclaimedCouponCount);

        assertThat(result.getReceiveCount()).isEqualTo(receiveCount);
        assertThat(result.getTotalFailCount()).isEqualTo(failCount);
        assertThat(unclaimedCouponCount).isEqualTo(leftCount);
        couponRedisTemplate.delete(couponPolicy.getId().toString());
    }

    @ParameterizedTest
    @MethodSource("eventParameters")
    @DisplayName("멀티 스레드 이벤트 쿠폰 발급 테스트")
    void multiThreadProvideEventCouponTest(int issueCount, int requestCount, int mismatchCount) throws Exception {
        final Result result = new Result();

        CouponTypeCash couponTypeCash = couponTypeCashRepository.save(te.getCouponTypeCash_1000_10000());
        CouponUsageAll couponUsageAll = couponUsageAllRepository.save(te.getCouponUsageAll());

        CouponPolicy couponPolicy = couponPolicyRepository.save(te.getCouponPolicy(couponTypeCash, couponUsageAll));

        couponRedisTemplate.delete(couponPolicy.getId().toString());

        CreateIssueCouponRequestDto createIssueCouponRequestDto =
                te.createUsingDeclared(CreateIssueCouponRequestDto.class);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "couponPolicyId", couponPolicy.getId());
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", (long) issueCount);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueMethod", IssueMethod.EVENT);

        issueCouponService.createIssueCoupon(createIssueCouponRequestDto);

        AccountStatus accountStatus = accountStatusRepository.save(new AccountStatus("OK", "허용"));
        Authority authority = authorityRepository.save(new Authority("NML", "일반"));
        Rank rank = rankRepository.save(new Rank("L1", "1레벨"));

        CountDownLatch countDownLatch = new CountDownLatch(requestCount);

        BoundSetOperations<String, Object> redisSet = couponRedisRepository.getRedisSet(couponPolicy.getId().toString());

        for (int i = 0; i < mismatchCount; i++) {
            redisSet.pop();
        }

        Stream.generate(() -> accountRepository.save(
                        new Account(accountStatus, authority, rank, "eora21", "pw",
                                "김주호", "어라?!", "eora21@naver.com", LocalDate.now(),
                                "01012345678")))
                .limit(requestCount)
                .map(account ->
                        new Thread(new Provider(result, countDownLatch,
                                createUpdateProvideCouponRequestDto(couponPolicy.getId(), account.getId()),
                            provideCouponService::provideCouponToAccountByEvent)))
                .forEach(Thread::start);

        countDownLatch.await();

        int receiveCount = Math.min(issueCount, requestCount);
        int failCount = Math.max(requestCount - issueCount, 0);
        int leftCount = Math.max(issueCount - requestCount, 0);

        Long unclaimedCouponCount = couponPolicyRepository.lookupUnclaimedCouponCount(couponPolicy.getId());

        System.out.println("receive:" + result.getReceiveCount());
        System.out.println("fail:" + result.getTotalFailCount());
        System.out.println("exhaustion:" + result.getExhaustionCount());
        System.out.println("provideFail:" + result.getProvideFailCount());
        System.out.println("unclaimed:" + unclaimedCouponCount);

        assertThat(result.getReceiveCount()).isEqualTo(receiveCount);
        assertThat(result.getTotalFailCount()).isEqualTo(failCount);
        assertThat(unclaimedCouponCount).isEqualTo(leftCount);
        couponRedisTemplate.delete(couponPolicy.getId().toString());
    }

    public static Stream<Arguments> normalParameters() throws Throwable {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(1, 100),
            Arguments.of(8, 100),
            Arguments.of(80, 100),
            Arguments.of(800, 1000),
            Arguments.of(80, 80),
            Arguments.of(80, 20)
        );
    }

    public static Stream<Arguments> eventParameters() throws Throwable {
        return Stream.of(
                Arguments.of(1, 1, 1),
                Arguments.of(1, 100, 1),
                Arguments.of(8, 100, 5),
                Arguments.of(80, 100, 27),
                Arguments.of(800, 1000, 421),
                Arguments.of(80, 80, 3),
                Arguments.of(80, 20, 6)
        );
    }

    @RequiredArgsConstructor
    static class Provider implements Runnable {
        private final Result result;
        private final CountDownLatch countDownLatch;
        private final UpdateProvideCouponRequestDto updateProvideCouponRequestDto;
        private final Consumer<UpdateProvideCouponRequestDto> consumer;

        @Override
        public void run() {
            try {
                consumer.accept(updateProvideCouponRequestDto);
                result.receive();
            } catch (CouponExhaustionException ignore) {
                result.exhaustion();
            } catch (ProvideIssueCouponFailureException ignore) {
                result.provideFail();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    static class Result {
        private final AtomicInteger receiveCount = new AtomicInteger(0);
        private final AtomicInteger exhaustionCount = new AtomicInteger(0);
        private final AtomicInteger provideFailCount = new AtomicInteger(0);

        public void receive() {
            receiveCount.getAndIncrement();
        }

        public void exhaustion() {
            exhaustionCount.getAndIncrement();
        }

        public void provideFail() {
            provideFailCount.getAndIncrement();
        }

        public int getReceiveCount() {
            return receiveCount.get();
        }

        public int getExhaustionCount() {
            return exhaustionCount.get();
        }

        public int getProvideFailCount() {
            return provideFailCount.get();
        }

        public int getTotalFailCount() {
            return exhaustionCount.get() + provideFailCount.get();
        }
    }
}




