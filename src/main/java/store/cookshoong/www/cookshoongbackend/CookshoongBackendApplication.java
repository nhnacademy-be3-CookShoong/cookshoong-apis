package store.cookshoong.www.cookshoongbackend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Slf4j
@SpringBootApplication
public class CookshoongBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookshoongBackendApplication.class, args);
		log.info("System Activated");
    }

}
