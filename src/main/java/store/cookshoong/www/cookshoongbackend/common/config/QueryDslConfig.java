package store.cookshoong.www.cookshoongbackend.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Data Jpa 는 JpaRepository 를 상속한 Repository 클래스에서
 * CustomRepository 에서 Custom Repository 기능을 사용할 수 있도록 하는 기능을 제공 Config.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Configuration
public class QueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
