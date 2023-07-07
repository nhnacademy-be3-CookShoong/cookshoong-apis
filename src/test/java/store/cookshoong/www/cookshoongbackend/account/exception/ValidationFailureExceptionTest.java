package store.cookshoong.www.cookshoongbackend.account.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 예외 동작 테스트
 *
 * @author koesnam
 * @since 2023.07.06
 */
class ValidationFailureExceptionTest {
    @Test
    @DisplayName("unmodifiableMap 에 내부값은 추가가 불가능하다.")
    void test_1() {
        Map<String, String> testMap = Collections.unmodifiableMap(new HashMap<>());

        assertThrows(UnsupportedOperationException.class, () -> testMap.put("key", "value"));
    }

    @Test
    @DisplayName("final 로 정의한 Map 의 내부값은 변경 가능하다.")
    void test_2() {
        final Map<String, String> testMap = new HashMap<>();

        assertDoesNotThrow(() -> testMap.put("key", "value"));
    }
}
