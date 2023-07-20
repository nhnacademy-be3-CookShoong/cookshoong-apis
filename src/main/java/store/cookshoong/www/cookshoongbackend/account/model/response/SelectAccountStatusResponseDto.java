package store.cookshoong.www.cookshoongbackend.account.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountStatusDto;

/**
 * 회원의 현재 상태조회에 대한 응답 객체.
 *
 * @author koesnam (추만석)
 * @since 2023.07.19
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectAccountStatusResponseDto {
    private String status;

    /**
     * 조회한 회원상태 객체를 응답용 객체로 변환.
     *
     * @param statusDto the status dto
     * @return the select account status response dto
     */
    public static SelectAccountStatusResponseDto responseDtoFrom(SelectAccountStatusDto statusDto) {
        return new SelectAccountStatusResponseDto(statusDto.getStatus().getStatusCode());
    }
}
