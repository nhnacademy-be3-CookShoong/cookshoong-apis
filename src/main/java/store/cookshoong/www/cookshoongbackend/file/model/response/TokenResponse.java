package store.cookshoong.www.cookshoongbackend.file.model.response;

import antlr.Token;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * POST    https://api-identity-infrastructure.nhncloudservice.com/v2.0/tokens 에 대한 응답. <br>
 * 토큰 발급 요청에 대한 응답 json에서 토큰id와 만료시간을 확인 가능. <br>
 * { <br>
 *   "access": { <br>
 *     "token": { <br>
 *       "expires": "{Expires Time}", <br>
 *       "id": "{token-id}", <br>
 *       ... <br>
 *     }
 *   }
 * }
 *
 * @author seungyeon (유승연)
 * @since 2023.07.26
 */
@Data
public class TokenResponse {
    private Access access;

    @Data
    public static class Access{
        Token token;
    }

    @Data
    public static class Token{
        LocalDateTime expires;
        String id;
    }

}
