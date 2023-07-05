package store.cookshoong.www.cookshoongbackend.account.model.vo;

/**
 * 회원 상태코드들을 상수로 관리하기 위한 Enum
 *
 * @author koesnam
 * @since 2023.07.05
 */
public enum AccountStatusCode {
    ACTIVE("활성"),
    DORMANCY("휴면"),
    WITHDRAWAL("탈퇴");
    private final String description;

    public String getDescription() {
        return description;
    }

    AccountStatusCode(String description) {
        this.description = description;
    }
}
