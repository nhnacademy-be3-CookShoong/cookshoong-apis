package store.cookshoong.www.cookshoongbackend.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import store.cookshoong.www.cookshoongbackend.common.property.SecureKeyManagerProperties;

/**
 * API 호출을 위한 기본 RestTemplate 빈 등록하기 위한 Configuration Class.
 *
 * @author koesnam
 * @since 2023.07.10
 */
@Configuration
public class RestTemplateConfig {
    /**
     * 공통 RestTemplate 등록.
     *
     * @return the rest template
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    /**
     * 클라이언트 인증서를 담은 RestTemplate.
     * (사용시 필드명을 sslRestTemplate 으로 해줘야 주입된다.)
     *
     * @param properties the properties
     * @return the rest template
     * @throws KeyStoreException         the key store exception
     * @throws CertificateException      the certificate exception
     * @throws IOException               the io exception
     * @throws NoSuchAlgorithmException  the no such algorithm exception
     * @throws UnrecoverableKeyException the unrecoverable key exception
     * @throws KeyManagementException    the key management exception
     */
    @Bean
    public RestTemplate sslRestTemplate(SecureKeyManagerProperties properties) throws KeyStoreException,
        CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        String password = properties.getPassword();

        RestTemplate sslRestTemplate = new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();

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
        sslRestTemplate.setRequestFactory(requestFactory);

        return sslRestTemplate;
    }
}
