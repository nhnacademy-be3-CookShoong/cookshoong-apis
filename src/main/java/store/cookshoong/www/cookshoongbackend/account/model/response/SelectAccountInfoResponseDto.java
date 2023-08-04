package store.cookshoong.www.cookshoongbackend.account.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰을 발급하기 위한 최소한이 정보를 응답하는 객체.
 * (현재 OAuth2 회원을 위해 쓰임.)
 *
 * @author koesnam (추만석)
 * @since 2023.08.01
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectAccountInfoResponseDto {
    private Long accountId;
    private String loginId;
    private String authority;
    private String status;

    /**
     * 회원 정보 응답 객체의 모든 인자를 받는 생성자.
     *
     * @param accountId the account id
     * @param loginId   the login id
     * @param authority the authority
     * @param status    the status
     */
    @QueryProjection
    public SelectAccountInfoResponseDto(Long accountId, String loginId, String authority, String status) {
        this.accountId = accountId;
        this.loginId = loginId;
        this.authority = authority;
        this.status = status;
    }
}
