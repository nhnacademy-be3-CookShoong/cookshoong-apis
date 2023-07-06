package store.cookshoong.www.cookshoongbackend.account.model.vo;

/**
 * 회원 등급코드들을 상수로 관리하기 위한 Enum.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public enum RankCode {
    LEVEL_1("프랜드"),
    LEVEL_2("패밀리"),
    LEVEL_3("마스터"),
    LEVEL_4("VIP");

    private final String description;

    public String getDescription() {
        return description;
    }

    RankCode(String description) {
        this.description = description;
    }
}
