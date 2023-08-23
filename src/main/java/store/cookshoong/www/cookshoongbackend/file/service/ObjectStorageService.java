package store.cookshoong.www.cookshoongbackend.file.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.common.property.ObjectStorageProperties;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
import store.cookshoong.www.cookshoongbackend.file.model.ThumbnailManager;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;

/**
 * 파일을 업로드, 다운로드 관련 업무 Object Storage 를 이용하여 처리.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.26
 */
@Service
@RequiredArgsConstructor
public class ObjectStorageService implements FileUtils {
    private final ObjectStorageAuth objectStorageAuth;
    private final ObjectStorageProperties objectStorageProperties;
    private final ImageRepository imageRepository;
    private final RestTemplate restTemplate;
    private final ThumbnailManager thumbnailManager;

    @Override
    public String getStorageType() {
        return LocationType.OBJECT_S.getVariable();
    }

    /**
     * 파일이 저장된 전체 경로로 생성.
     *
     * @param objectPath the object path
     * @param fileName   the file name
     * @return 전체 경로
     */
    @Override
    public String getFullPath(String objectPath, String fileName) {
        return String.format("%s/%s/%s/%s",
            objectStorageProperties.getStorageUrl(),
            objectStorageProperties.getContainerName(),
            objectPath,
            fileName);
    }

    @Override
    public String getSavedPath(String objectPath, String fileName) {
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

    private InputStream resizeImage(byte[] originalImageBytes, int targetWidth, int targetHeight, String type) throws IOException {
        BufferedImage resizedImage = Thumbnails.of(new ByteArrayInputStream(originalImageBytes))
            .size(targetWidth, targetHeight)
            .asBufferedImage();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, type, outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
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
        RestTemplate template = new RestTemplate(requestFactory);

        HttpMessageConverterExtractor<String> responseExtractor
            = new HttpMessageConverterExtractor<>(String.class, template.getMessageConverters());

        template.execute(url, HttpMethod.PUT, requestCallback, responseExtractor);
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

        if (thumbnailManager.isImageContainsThumb(domainName)) {
            String thumbUrl = getSavedPath(thumbnailManager.getThumbnailDomain(domainName), savedName);
            InputStream thumbnailInputStream =
                resizeImage(multipartFile.getBytes(), 300, 300, extractExt(originFileName));
            uploadObject(thumbnailInputStream, thumbUrl, token);
        }

        String url = getSavedPath(domainName, savedName);
        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());

        uploadObject(inputStream, url, token); // 업로드

        return imageRepository.save(new Image(getStorageType(), domainName, originFileName, savedName, isPublic));
    }

    @Override
    public Image updateFile(MultipartFile multipartFile, Image image) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String token = objectStorageAuth.requestToken();

        String originFileName = multipartFile.getOriginalFilename();
        if (Objects.isNull(originFileName)) {
            throw new NullPointerException();
        }

        String url = getSavedPath(image.getDomainName(), image.getSavedName());
        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());

        uploadObject(inputStream, url, token);

        image.updateImageInfo(originFileName);

        return image;
    }

    @Override
    public void deleteFile(Image image) throws IOException {
        String token = objectStorageAuth.requestToken(); // 토큰 가져오기
        if (thumbnailManager.isImageContainsThumb(image.getDomainName())) {
            String thumbUrl = getSavedPath(thumbnailManager.getThumbnailDomain(image.getDomainName()), image.getSavedName());
            deleteObject(thumbUrl, token);
        }
        String url = getSavedPath(image.getDomainName(), image.getSavedName());
        deleteObject(url, token);
    }

    private void deleteObject(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        HttpEntity<String> requestHttpEntity = new HttpEntity<>(null, headers);

        this.restTemplate.exchange(url, HttpMethod.DELETE, requestHttpEntity, String.class);
    }
}
