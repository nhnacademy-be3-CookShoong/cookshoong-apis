package store.cookshoong.www.cookshoongbackend.account.model.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * 권한 코드 테스트.
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

    @Test
    @DisplayName("권한 이름으로 AuthorityCode 객체를 얻을 수 있다.")
    void extract() {
        assertThat(AuthorityCode.ADMIN,
            is(sameInstance(AuthorityCode.valueOf("ADMIN"))));

        assertThat(AuthorityCode.ADMIN,
            is(not(sameInstance(AuthorityCode.valueOf("CUSTOMER")))));
    }

    @Test
    @DisplayName("없는 권한이름으로 값을 얻으려고 하면 예외를 발생시킨다.")
    void extract_2() {
        assertThrows(IllegalArgumentException.class,
            () -> AuthorityCode.valueOf("user"));
    }
}
