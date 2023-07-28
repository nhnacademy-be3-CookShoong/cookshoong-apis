package store.cookshoong.www.cookshoongbackend.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.TestPropertySource;

/**
 * 통합 테스트시 사용해줘야할 클래스.
 *
 * @author koesnam (추만석)
 * @since 2023.07.28
 */
@Import(ElasticSearchConfig.class)
@EnableAutoConfiguration(exclude = {EurekaClientAutoConfiguration.class, EurekaDiscoveryClientConfiguration.class})
@SpringBootTest
public class IntegrationTestBase {
}
