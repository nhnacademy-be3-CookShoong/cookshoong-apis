package store.cookshoong.www.cookshoongbackend.swagger.controller;

import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.swagger.service.SwaggerUiService;

/**
 * swagger json 데이터를 반환하는 controller.
 *
 * @author eora21
 * @since 2023.08.22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/swagger-ui")
public class SwaggerUiController {
    private final SwaggerUiService swaggerUiService;

    /**
     * 생성된 open api 반환.
     *
     * @return the open api json
     * @throws IOException the io exception
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOpenApiJson() throws IOException {
        return ResponseEntity.ok(swaggerUiService.createSwaggerUiResponse());
    }
}
