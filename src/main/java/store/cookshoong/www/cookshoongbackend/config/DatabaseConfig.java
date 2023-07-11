package store.cookshoong.www.cookshoongbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import store.cookshoong.www.cookshoongbackend.common.model.response.SecureKeyManagerResponseDto;
import store.cookshoong.www.cookshoongbackend.common.property.DatabaseProperties;
import store.cookshoong.www.cookshoongbackend.common.property.SecureKeyManagerProperties;

/**
 * DB 설정에 대한 Configuration Class.
 * DB 접속정보는 Secure Key Manager 를 사용하여 가져온다.
 *
 * @author koesnam
 * @since 2023.07.10
 */
@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {
    private final RestTemplate restTemplate;

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
            .build();
    }

    /**
     * SKM 로 부터 인증서를 보내 DB 설정값들을 가져온다.
     *
     * @param secureKeyManagerProperties SKM 인증정보
     * @return DB 설정값
     * @throws KeyStoreException         the key store exception
     * @throws CertificateException      the certificate exception
     * @throws IOException               the io exception
     * @throws NoSuchAlgorithmException  the no such algorithm exception
     * @throws UnrecoverableKeyException the unrecoverable key exception
     * @throws KeyManagementException    the key management exception
     */
    @Bean
    @Profile("!default")
    public DatabaseProperties databaseProperties(SecureKeyManagerProperties secureKeyManagerProperties)
        throws KeyStoreException, CertificateException, IOException,
        NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {

        String password = secureKeyManagerProperties.getPassword();
        String keyid = secureKeyManagerProperties.getMysqlKeyid();
        String appkey = secureKeyManagerProperties.getAppkey();

        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        InputStream inputStream = new ClassPathResource("CookShoong.p12").getInputStream();
        clientStore.load(inputStream, password.toCharArray());
        SSLContext sslContext = SSLContextBuilder.create()
            .setProtocol("TLS")
            .loadKeyMaterial(clientStore, password.toCharArray())
            .loadTrustMaterial(new TrustSelfSignedStrategy())
            .build();

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLSocketFactory(sslConnectionSocketFactory)
            .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);

        URI uri = UriComponentsBuilder
            .fromUriString("https://api-keymanager.nhncloudservice.com/keymanager/v1.0/appkey/{appkey}/secrets/{keyid}")
            .uriVariables(Map.of("appkey", appkey, "keyid", keyid))
            .encode()
            .build()
            .toUri();

        String response = Objects.requireNonNull(restTemplate
            .getForEntity(uri, SecureKeyManagerResponseDto.class).getBody()
            )
            .getResponseBody()
            .getSecrets();

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, DatabaseProperties.class);
    }
}

