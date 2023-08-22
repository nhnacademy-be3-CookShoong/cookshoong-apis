package store.cookshoong.www.cookshoongbackend.swagger.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * swagger-ui 서비스.
 *
 * @author eora21 (김주호)
 * @since 2023.08.22
 */
@Service
@RequiredArgsConstructor
public class SwaggerUiService {
    private static final String OPEN_API_JSON = "/static/swagger-ui/openapi-3.0.json";

    private final ObjectMapper objectMapper;

    /**
     * swagger-ui를 위한 openapi json 반환.
     *
     * @return the map
     * @throws IOException the io exception
     */
    public Map<String, Object> createSwaggerUiResponse() throws IOException {
        Resource resource = new ClassPathResource(OPEN_API_JSON);
        return objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
    }
}
