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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import store.cookshoong.www.cookshoongbackend.common.property.DatabaseProperties;
import store.cookshoong.www.cookshoongbackend.common.model.response.SecureKeyManagerResponseDto;
import store.cookshoong.www.cookshoongbackend.common.property.SecureKeyManagerProperties;

/**
 * DB 설정에 대한 Configuration Class.
 *
 * @author koesnam
 * @since 2023.07.10
 */
@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {
    private final RestTemplate restTemplate;

    @Bean
    public DataSource dataSource(DatabaseProperties databaseProperties) {
        return DataSourceBuilder.create()
            .driverClassName(databaseProperties.getDriverClassName())
            .url(databaseProperties.getUrl())
            .username(databaseProperties.getUsername())
            .password(databaseProperties.getPassword())
            .build();
    }

    @Bean
    public DatabaseProperties databaseProperties(SecureKeyManagerProperties secureKeyManagerProperties) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        String password = secureKeyManagerProperties.getPassword();
        String keyid = secureKeyManagerProperties.getKeyid();
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

        String response = Objects.requireNonNull(restTemplate.getForEntity(uri,
                    SecureKeyManagerResponseDto.class)
                .getBody())
            .getResponseBody()
            .getSecrets();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, DatabaseProperties.class);
    }
}

