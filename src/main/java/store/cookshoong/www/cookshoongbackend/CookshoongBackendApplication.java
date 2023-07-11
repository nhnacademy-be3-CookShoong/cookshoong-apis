package store.cookshoong.www.cookshoongbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * The type Cookshoong backend application.
 */
@SpringBootApplication
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
