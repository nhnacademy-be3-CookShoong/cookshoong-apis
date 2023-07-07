package store.cookshoong.www.cookshoongbackend.util;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountsStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.store.entity.BankType;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreStatus;

/**
 * 테스트 환경에서 원하는 entity 미리 만들어두는 클래스.
 * 간편하게 재활용하여 사용할 수 있도록 함.
 *
 * @author eora21
 * @since 2023.07.07
 */
public class TestEntity {
    public static Address ADDRESS = new Address("main", "detail", BigDecimal.ONE, BigDecimal.ZERO);
    public static AccountsStatus ACCOUNTS_STATUS_ACTIVE = new AccountsStatus("ACTIVE", "활성");
    public static Authority AUTHORITY_CUSTOMER = new Authority("CUSTOMER", "일반회원");
    public static Rank RANK_LEVEL_ONE = new Rank("LEVEL_1", "프렌드");
    public static Account ACCOUNT_ACTIVE_CUSTOMER_LEVEL_ONE = new Account(ACCOUNTS_STATUS_ACTIVE, AUTHORITY_CUSTOMER,
        RANK_LEVEL_ONE, "eora21", "pwd", "김주호", "말비묵", "test@test.com",
        LocalDate.of(1996, 4, 1), "01012345678");
    public static BankType BANK_TYPE_KB = createTestBankType("KB", "국민은행");
    public static StoreStatus STORE_STATUS_OPEN = createTestStoreStatus("OPEN", "영업중");
    public static Store STORE_OPEN = new Store(null, ACCOUNT_ACTIVE_CUSTOMER_LEVEL_ONE, BANK_TYPE_KB,
        STORE_STATUS_OPEN, "license", "123456", "김주호",
        LocalDate.of(2020, 2, 20), "주호타코", "01012345678", BigDecimal.ONE,
        null, null, "123456");

    private static BankType createTestBankType(String bankTypeCode, String description) {
        BankType bankType = createEntityUsingDeclared(BankType.class);
        ReflectionTestUtils.setField(bankType, "bankTypeCode", bankTypeCode);
        ReflectionTestUtils.setField(bankType, "description", description);
        return bankType;
    }

    private static StoreStatus createTestStoreStatus(String storeStatusCode, String description) {
        StoreStatus storeStatus = createEntityUsingDeclared(StoreStatus.class);
        ReflectionTestUtils.setField(storeStatus, "storeStatusCode", "ssc");
        ReflectionTestUtils.setField(storeStatus, "description", "desc");
        return storeStatus;
    }

    private static <T> T createEntityUsingDeclared(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
