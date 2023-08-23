package store.cookshoong.www.cookshoongbackend.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import store.cookshoong.www.cookshoongbackend.common.property.DatabaseProperties;
import store.cookshoong.www.cookshoongbackend.common.service.SKMService;

/**
 * DB 설정에 대한 Configuration Class.
 * DB 접속정보는 Secure Key Manager 를 사용하여 가져온다.
 *
 * @author koesnam
 * @since 2023.07.10
 */
@Configuration
public class DatabaseConfig {
    /**
     * DB 설정을 마친 Datasource.
     *
     * @param databaseProperties DB 설정값
     * @return the data source
     */
    @Bean
    @Profile("!default")
    public DataSource dataSource(DatabaseProperties databaseProperties) {
        return DataSourceBuilder.create()
            .driverClassName(databaseProperties.getDriverClassName())
            .url(databaseProperties.getUrl())
            .username(databaseProperties.getUsername())
            .password(databaseProperties.getPassword())
            .type(BasicDataSource.class)
            .build();
    }

    /**
     * SKM 로 부터 클라이언트 인증서를 보내 DB 설정값들을 가져온다.
     *
     * @param mysqlKeyid SKM 저장되있는 기밀 데이터의 아이디
     * @return DB 설정값
     * @throws JsonProcessingException the json processing exception
     */
    @Bean
    @Profile("!default")
    public DatabaseProperties databaseProperties(@Value("${cookshoong.skm.keyid.mysql}") String mysqlKeyid,
                                                 SKMService skmService) throws JsonProcessingException {
        return skmService.fetchSecrets(mysqlKeyid, DatabaseProperties.class);
    }
}


