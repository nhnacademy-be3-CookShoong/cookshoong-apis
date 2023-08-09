package store.cookshoong.www.cookshoongbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * The type Cookshoong backend application.
 */
@EnableAsync
@SpringBootApplication
@EnableDiscoveryClient
@ConfigurationPropertiesScan
public class CookshoongBackendApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CookshoongBackendApplication.class, args);
    }
}
