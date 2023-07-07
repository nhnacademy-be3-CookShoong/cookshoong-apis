package store.cookshoong.www.cookshoongbackend.account.model.vo;

import java.util.Arrays;

/**
 * 권한 코드를 상수로 관리.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public enum AuthorityCode {
    CUSTOMER, BUSINESS, ADMIN;

    public static boolean matches(String authorityCode) {
        return Arrays.stream(AuthorityCode.values())
            .anyMatch(a -> a.name().equals(authorityCode));
    }
}
