package store.cookshoong.www.cookshoongbackend.common.util;

/**
 * 검증 로직에 위반되었을 때 반환되는 메세지들.
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
    public static final String MAIN_DETAIL_ADDRESS = "옳지 않은 주소 입니다. 다시 입력해주세요.";

    public static final String ENGLISH_UPPER_ONLY = "영어 대문자만 입력해주세요.";
    public static final String KOREAN_WITH_BLANK = "한국어와 빈칸만 입력해주세요";
}
