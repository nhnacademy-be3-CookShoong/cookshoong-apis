package store.cookshoong.www.cookshoongbackend.file.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.common.property.ObjectStorageProperties;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;

/**
 * 파일을 업로드, 다운로드 관련 업무 Object Storage 를 이용하여 처리.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.26
 */
@Service
@RequiredArgsConstructor
public class ObjectStorageService implements FileService {
    private final ObjectStorageAuth objectStorageAuth;
    private final ObjectStorageProperties objectStorageProperties;
    private final ImageRepository imageRepository;


    /**
     * 파일이 저장된 전체 경로로 생성.
     *
     * @param objectPath the object path
     * @param fileName   the file name
     * @return 전체 경로
     */
    public String getFullPath(String objectPath, String fileName) {
        return String.format("%s/%s/%s/%s",
            objectStorageProperties.getStorageUrl(),
            objectStorageProperties.getContainerName(),
            objectPath,
            fileName);
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    /**
     * Object Storage로 업로드.
     *
     * @param inputStream the input stream
     * @param url         the url
     * @param token       the token
     */
    public void uploadObject(final InputStream inputStream, String url, String token) {
        final RequestCallback requestCallback = request -> {
            request.getHeaders().add("X-Auth-Token", token);
            IOUtils.copy(inputStream, request.getBody());
        };

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        HttpMessageConverterExtractor<String> responseExtractor
            = new HttpMessageConverterExtractor<>(String.class, restTemplate.getMessageConverters());

        restTemplate.execute(url, HttpMethod.PUT, requestCallback, responseExtractor);
    }

    @Override
    public Image storeFile(MultipartFile multipartFile, String domainName, boolean isPublic) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String token = objectStorageAuth.requestToken(); // 토큰 가져오기

        String originFileName = multipartFile.getOriginalFilename(); // 파일 업로드시 이름
        if (Objects.isNull(originFileName)) {
            throw new NullPointerException();
        }
        String savedName = UUID.randomUUID() + "." + extractExt(originFileName); // 저장된 이름

        String url = getFullPath(domainName, savedName);
        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());

        uploadObject(inputStream, url, token); // 업로드

        return imageRepository.save(new Image(originFileName, savedName, isPublic));
    }

}
