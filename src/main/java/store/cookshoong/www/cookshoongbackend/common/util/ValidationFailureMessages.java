package store.cookshoong.www.cookshoongbackend.common.util;

/**
 * 검증 로직에 위반되었을 때 반환되는 메세지들
 *
 * @author koesnam
 * @since 2023.07.05
 */

public class ValidationFailureMessages {
    private ValidationFailureMessages() {
        throw new IllegalStateException("It's util class");
    }
    public static final String LETTER_ONLY = "문자만 입력해주세요";
    public static final String LETTER_WITH_NUMBER = "문자와 숫자만 입력해주세요";
    public static final String NUMBER_ONLY = "숫자만 입력해주세요";
    public static final String NOT_NUMBER = "숫자를 제외한 문자만 입력해주세요";
}
