package store.cookshoong.www.cookshoongbackend.common.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Secure Key Manager 로 부터 DB 정보를 가져오기 위한 DTO.
 *
 * @author koesnam
 * @since 2023.07.10
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecureKeyManagerResponseDto {
    @JsonProperty("body")
    private SecureKeyManagerResponseBody responseBody;

    /**
     * ex)
     * "body" : {
     *     "secret" : { ... }
     * }
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SecureKeyManagerResponseBody {
        @JsonProperty("secret")
        private String secrets;

        /**
         * ex)
         * "secret" : {
         *     "datasource" : {
         *         ...
         *     }
         * }
         */
        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Secrets {
            @JsonProperty("datasource")
            private String databaseProperties;
        }
    }
}



