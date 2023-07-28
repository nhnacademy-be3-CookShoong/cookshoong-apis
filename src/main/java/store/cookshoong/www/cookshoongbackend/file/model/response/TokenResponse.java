package store.cookshoong.www.cookshoongbackend.file.model.response;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * POST    https://api-identity-infrastructure.nhncloudservice.com/v2.0/tokens 에 대한 응답. <br>
 * 토큰 발급 요청에 대한 응답 json에서 토큰id와 만료시간을 확인 가능. <br>
 * { <br>
 * "access": { <br>
 * "token": { <br>
 * "expires": "{Expires Time}", <br>
 * "id": "{token-id}", <br>
 * ... <br>
 * }
 * }
 * }
 *
 * @author seungyeon (유승연)
 * @since 2023.07.26
 */
@Getter
public class TokenResponse {
    private Access access;

    @Getter
    public static class Access {
        Token token;
    }

    @Getter
    public static class Token {
        LocalDateTime expires;
        String id;
    }

}
