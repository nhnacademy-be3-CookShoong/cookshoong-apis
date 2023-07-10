package store.cookshoong.www.cookshoongbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * swagger-ui 화면을 보여주기 위한 WebMvcConfigurer.
 *
 * @author eora21
 * @since 2023.07.09
 */
@Configuration
public class SwaggerRoutingConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/swagger-ui/");
        registry.addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/static/swagger-ui/swagger-ui.html");
    }
}
