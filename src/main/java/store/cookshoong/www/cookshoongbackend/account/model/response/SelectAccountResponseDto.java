package store.cookshoong.www.cookshoongbackend.account.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 회원의 모든 정보를 전달하는 Dto.
 *
 * @author koesnam
 * @since 2023.07.08
 */
@Getter
public class SelectAccountResponseDto {
    private final Long id;
    private final String status;
    private final String authority;
    private final String rank;
    private final String loginId;
    private final String name;
    private final String nickname;
    private final String email;
    private final LocalDate birthday;
    private final String phoneNumber;
    private final LocalDateTime lastLoginAt;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param id          the id
     * @param status      the status
     * @param authority   the authority
     * @param rank        the rank
     * @param loginId     the login id
     * @param name        the name
     * @param nickname    the nickname
     * @param email       the email
     * @param birthday    the birthday
     * @param phoneNumber the phone number
     * @param lastLoginAt the last login at
     */
    @QueryProjection
    public SelectAccountResponseDto(Long id, String status, String authority,
                                    String rank, String loginId, String name,
                                    String nickname, String email, LocalDate birthday,
                                    String phoneNumber, LocalDateTime lastLoginAt) {
        this.id = id;
        this.status = status;
        this.authority = authority;
        this.rank = rank;
        this.loginId = loginId;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.lastLoginAt = lastLoginAt;
    }
}
