package store.cookshoong.www.cookshoongbackend.account.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 회원 상태 변경시 응답 객체.
 *
 * @author koesnam (추만석)
 * @since 2023.07.29
 */
@Getter
@AllArgsConstructor
public class UpdateAccountStatusResponseDto {
    private String status;
    private LocalDateTime updatedAt;
}
