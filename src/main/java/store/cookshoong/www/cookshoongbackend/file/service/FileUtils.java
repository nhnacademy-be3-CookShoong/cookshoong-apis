package store.cookshoong.www.cookshoongbackend.file.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;

/**
 * 파일을 다루기 위한 인터페이스.
 * LocalFileService 는 로컬 저장소,
 * ObjectStorageService 는 오브젝트 스토리지를 사용합니다.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.26
 */
public interface FileUtils {
    /**
     * 저장소 타입.
     *
     * @return the storage type
     */
    String getStorageType();

    /**
     * 해당하는 저장소 타입.
     *
     * @param locationType the location type
     * @return the boolean
     */
    default boolean matchStorageType(String locationType) {
        return locationType.equals(getStorageType());
    }

    /**
     * 이미지 업로드시 사용되는 메서드.
     *
     * @param multipartFile the multipart file
     * @param domainName    the domain name
     * @param isPublic      the is public
     * @return the image
     * @throws IOException the io exception
     */
    Image storeFile(MultipartFile multipartFile, String domainName, boolean isPublic) throws IOException;

    /**
     * 저장된 이미지의 전체 경로.
     *
     * @param domain   the domain
     * @param filename the filename
     * @return the full path
     */
    String getFullPath(String domain, String filename);
}
