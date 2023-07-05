package store.cookshoong.www.cookshoongbackend.account.model.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * 권환 코드 테스트.
 *
 * @author koesnam
 * @since 2023.07.05
 */
class AuthorityCodeTest {
    @Test
    @DisplayName("권한은 CUSTOMER, BUSINESS, ADMIN 만 존재한다.")
    void matches() {
        assertTrue(AuthorityCode.matches("CUSTOMER"));
        assertTrue(AuthorityCode.matches("BUSINESS"));
        assertTrue(AuthorityCode.matches("ADMIN"));
    }

    @Test
    @DisplayName("권한은 CUSTOMER, BUSINESS, ADMIN 만 존재한다.")
    void matches_2() {
        assertFalse(AuthorityCode.matches("MASTER"));
        assertFalse(AuthorityCode.matches("MASTER"));
        assertFalse(AuthorityCode.matches("KING"));
    }

    @Test
    @DisplayName("권한은 대문자로만 구성되어있다.")
    void matches_3() {
        assertFalse(AuthorityCode.matches("customer"));
        assertFalse(AuthorityCode.matches("business"));
        assertFalse(AuthorityCode.matches("admin"));
    }
}
