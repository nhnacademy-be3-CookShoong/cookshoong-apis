package store.cookshoong.www.cookshoongbackend.file.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;

/**
 * 파일을 업로드, 다운로드 관련 업무 로컬저장소를 이용하여 처리.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.18
 */
@Service
@RequiredArgsConstructor
public class LocalFileService implements FileUtils {
    private final ImageRepository imageRepository;

    private final String rootPath = System.getProperty("user.dir");
    @Value("${file.save.base.path}")
    private String fileDir;

    @Override
    public String getStorageType() {
        return LocationType.LOCAL_S.getVariable();
    }

    /**
     * 파일 경로 가져오기 (root 경로 + 폴더명 + 파일이름).
     *
     * @param domain   the domain
     * @param filename the filename
     * @return the full path
     */
    @Override
    public String getFullPath(String domain, String filename) {
        return "/images/local?imageName=" + rootPath + fileDir + domain + "/" + filename;
    }

    @Override
    public String getSavedPath(String domain, String filename) {
        return rootPath + fileDir + domain + "/" + filename;
    }

    /**
     * 사용자가 이미지 파일 업로드 시 로컬에 저장.
     *
     * @param multipartFile the multipart file
     * @param isPublic      the is public
     * @return the image
     * @throws IOException the io exception
     */
    public Image storeFile(MultipartFile multipartFile, String domain, boolean isPublic) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originFilename = multipartFile.getOriginalFilename();
        String storeFilename = UUID.randomUUID() + "." + extractExt(Objects.requireNonNull(originFilename));

        multipartFile.transferTo(Paths.get(getSavedPath(domain, storeFilename)));
        return imageRepository.save(new Image(getStorageType(), domain, originFilename, storeFilename, isPublic));
    }

    private String extractExt(String originalFilename) { // 확장자명 가져오기
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    @Override
    public Image updateFile(MultipartFile multipartFile, Image image) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originFilename = multipartFile.getOriginalFilename();
        //해당 파일 경로에 파일 생성해서 덮어씌움.
        String existingFilePath = getSavedPath(image.getDomainName(), image.getSavedName());

        multipartFile.transferTo(Paths.get(existingFilePath));
        image.updateImageInfo(originFilename);
        return image;
    }

    @Override
    public void deleteFile(Image image) throws IOException {
        Path filePath = Paths.get(getSavedPath(image.getDomainName(), image.getSavedName()));
        Files.deleteIfExists(filePath);
    }
}
