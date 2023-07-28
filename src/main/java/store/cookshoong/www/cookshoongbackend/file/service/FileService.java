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
public interface FileService {
    Image storeFile(MultipartFile multipartFile, String domainName, boolean isPublic) throws IOException;
}
