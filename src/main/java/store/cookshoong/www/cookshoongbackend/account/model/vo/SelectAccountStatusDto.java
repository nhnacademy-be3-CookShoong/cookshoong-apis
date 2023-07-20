package store.cookshoong.www.cookshoongbackend.account.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;

/**
 * 회원의 현재 상태조회에 대한 응답 객체.
 *
 * @author koesnam (추만석)
 * @since 2023.07.19
 */
@Getter
@AllArgsConstructor
public class SelectAccountStatusDto {
    private AccountStatus status;
}
