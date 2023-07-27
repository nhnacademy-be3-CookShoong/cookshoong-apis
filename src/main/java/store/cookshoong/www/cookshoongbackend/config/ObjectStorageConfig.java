package store.cookshoong.www.cookshoongbackend.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import store.cookshoong.www.cookshoongbackend.common.property.ObjectStorageProperties;
import store.cookshoong.www.cookshoongbackend.common.service.SKMService;

/**
 * Object Storage 를 사용하기 위한 Config 클래스.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.26
 */
@Configuration
public class ObjectStorageConfig {
    @Bean
    @Profile("!default")
    public ObjectStorageProperties objectStorageProperties(@Value("${cookshoong.skm.keyid.object-storage}") String storageKeyId,
                                                   SKMService skmService) throws JsonProcessingException {
        return skmService.fetchSecrets(storageKeyId, ObjectStorageProperties.class);
    }
}
