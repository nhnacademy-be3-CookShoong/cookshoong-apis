package store.cookshoong.www.cookshoongbackend.common.util;

/**
 * 자주 쓰일 것 같은 정규표현식들.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public class RegularExpressions {
    private RegularExpressions() {
        throw new IllegalStateException("It's util class");
    }

    // 문자(한글 + 영어)만 (공백 허용x)
    public static final String LETTER_ONLY = "^[가-힇A-Za-z]+$";
    // 문자(한글 + 영어) + 숫자 (공백 허용x)
    public static final String LETTER_WITH_NUMBER = "^[가-힇\\w]+$";
    // 문자(한글 + 영어)만 (띄어쓰기 허용) + @NotBlank 사용하셔야 공백을 막을 수 있습니다.
    public static final String LETTER_ONLY_WITH_BLANK = "^[가-힇A-Za-z ]+$";
    // 문자(한글 + 영어)만 (띄어쓰기 허용) + 숫자 + @NotBlank 사용하셔야 공백을 막을 수 있습니다.
    public static final String LETTER_WITH_NUMBER_AND_BLANK = "^[가-힇A-Za-z0-9 ]+$";
    // 문자(한글 + 영어)만 (띄어쓰기 허용) + 숫자 + 특별문자  + @NotBlank 사용하셔야 공백을 막을 수 있습니다.
    public static final String MAIN_DETAIL_ADDRESS = "^[가-힇0-9\\w\\-\\, ]+$";
    // 숫자만 (1개 이상)
    public static final String NUMBER_ONLY = "^\\d+$";
    // 숫자가 아닌 모든 문자(한글, 영어, 특수문자) (1개 이상)
    public static final String NOT_NUMBER = "(?=^\\D+$)(?=^\\S+$).*";
}


