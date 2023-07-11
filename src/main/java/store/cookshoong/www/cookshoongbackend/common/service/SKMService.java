package store.cookshoong.www.cookshoongbackend.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import store.cookshoong.www.cookshoongbackend.common.model.response.SecureKeyManagerResponseDto;
import store.cookshoong.www.cookshoongbackend.common.property.SecureKeyManagerProperties;

/**
 * Secure Key Manager 서비스를 호출하기 위한 서비스 클래스.
 *
 * @author koesnam
 * @since 2023.07.11
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Service
@RequiredArgsConstructor
public class SKMService {
    private final RestTemplate sslRestTemplate;
    private final SecureKeyManagerProperties secureKeyManagerProperties;

    /**
     * SKM API 호출을 통해 저장해둔 키값들을 가져온다.
     *
     * @param <T>       저장해둔 키값과 형식이 맞는 타입
     * @param keyid     SKM 저장되있는 기밀 데이터의 아이디
     * @param valueType 저장해둔 키값과 형식이 맞는 타입
     * @return 키값
     * @throws JsonProcessingException the json processing exception
     */
    public <T> T fetchSecrets(String keyid, Class<T> valueType) throws JsonProcessingException {
        String appkey = secureKeyManagerProperties.getAppkey();

        URI uri = UriComponentsBuilder
            .fromUriString("https://api-keymanager.nhncloudservice.com/keymanager/v1.0/appkey/{appkey}/secrets/{keyid}")
            .uriVariables(Map.of("appkey", appkey, "keyid", keyid))
            .encode()
            .build()
            .toUri();

        String response = Objects.requireNonNull(sslRestTemplate
                .getForEntity(uri, SecureKeyManagerResponseDto.class).getBody()
            )
            .getResponseBody()
            .getSecrets();

        return extractSecrets(response, valueType);
    }

    /**
     * SKM 에 JSON 형식으로 값을 저장해뒀을 때, 제대로 값이 매핑되지 않아 String -> 원하는 타입으로 매핑하기 위한 메서드.
     */
    private <T> T extractSecrets(String response, Class<T> valueType) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, valueType);
    }
}
