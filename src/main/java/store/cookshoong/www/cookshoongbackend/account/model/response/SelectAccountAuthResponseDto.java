package store.cookshoong.www.cookshoongbackend.account.model.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountAuthDto;

/**
 * 회원의 모든 정보를 전달하는 Dto.
 *
 * @author koesnam
 * @since 2023.07.08
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectAccountAuthResponseDto {
    private String username;
    private String password;
    private Attributes attributes;

    /**
     * 저장된 정보에서 전송용 응답정보로 변환시켜주는 메서드.
     *
     * @param authDto 기존 회원정보 중에서 자격 증명과 함께 보내줄 정보
     * @return 응답시 보내줄 정보
     */
    public static SelectAccountAuthResponseDto responseDtoFrom(SelectAccountAuthDto authDto) {
        SelectAccountAuthResponseDto authResponseDto = new SelectAccountAuthResponseDto();
        authResponseDto.username = authDto.getLoginId();
        authResponseDto.password = authDto.getPassword();
        authResponseDto.attributes = Attributes.builder()
            .accountId(authDto.getId())
            .authority(authDto.getAuthority().getAuthorityCode())
            .status(authDto.getStatus().getStatusCode())
            .build();

        return authResponseDto;
    }

    /**
     * 유저 인증 정보 중 자격증명을 제외하고 부가적인 정보 담고 있는 클래스.
     */
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Attributes {
        private Long accountId;
        private String status;
        private String authority;
    }
}
