package store.cookshoong.www.cookshoongbackend.file.service;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import store.cookshoong.www.cookshoongbackend.common.property.ObjectStorageProperties;
import store.cookshoong.www.cookshoongbackend.file.model.request.TokenRequest;
import store.cookshoong.www.cookshoongbackend.file.model.response.TokenResponse;

/**
 * Object Storage에 TokenReuqest(인증정보)를 담아서 토큰 요청.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ObjectStorageAuth {
    private final RestTemplate restTemplate;
    private final ObjectStorageProperties objectStorageProperties;
    private String tokenId;
    private LocalDateTime expires;

    /**
     * 오브젝트 스토리지에 접근하기 위한 토큰 요청.
     *
     * @return the string
     */
    public String requestToken() {
        log.error("requestToken In");
        String authUrl = objectStorageProperties.getAuthUrl() + "/tokens";

        //토큰이 생성이 되어있다면 tokenId 그대로 반환
        if (Objects.nonNull(tokenId) && expires.isBefore(LocalDateTime.now())) {
            log.error("token already in");
            return tokenId;
        }

        // 토큰 만료시
        // 헤더 생성
        log.error("new header");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.error("tokenRequest start");
        //본문 생성
        TokenRequest tokenRequest = new TokenRequest(objectStorageProperties.getTenantId(),
            objectStorageProperties.getUsername(),
            objectStorageProperties.getPassword());

        log.error("new tokenRequest");

        HttpEntity<TokenRequest> httpEntity = new HttpEntity<>(tokenRequest, headers); // body, header
        log.error("requestToken Post");
        // 토큰 요청
        ResponseEntity<TokenResponse> response = this.restTemplate.exchange(authUrl,
            HttpMethod.POST,
            httpEntity,
            TokenResponse.class
        );

        // 토큰 ID, 만료시간 설정
        tokenId = Objects.requireNonNull(response.getBody()).getAccess().getToken().getId();
        expires = Objects.requireNonNull(response.getBody()).getAccess().getToken().getExpires();
        log.error("tokenId : {}", tokenId);
        log.error("expries: {}", expires);
        return tokenId;
    }

}
