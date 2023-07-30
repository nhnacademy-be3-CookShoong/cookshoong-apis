package store.cookshoong.www.cookshoongbackend.account.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;

/**
 * 회원의 모든 정보를 전달하는 Dto.
 *
 * @author koesnam
 * @since 2023.07.08
 */
@Getter
@AllArgsConstructor
public class SelectAccountAuthDto {
    private Long id;
    private String loginId;
    private String password;
    private Authority authority;
    private AccountStatus status;
}
