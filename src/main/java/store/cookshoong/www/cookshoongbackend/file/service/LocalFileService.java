package store.cookshoong.www.cookshoongbackend.file.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;

/**
 * 파일을 업로드, 다운로드 관련 업무 로컬저장소를 이용하여 처리.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.18
 */
@Component
@RequiredArgsConstructor
public class LocalFileService implements FileService {
    private final ImageRepository imageRepository;

    private final String rootPath = System.getProperty("user.dir");
    @Value("${file.save.base.path}")
    private String fileDir;

    /**
     * 파일 경로 가져오기.
     *
     * @param filename the filename
     * @return the full path
     */
    public String getFullPath(String filename) {
        return rootPath + fileDir +"/"+ filename;
    }

    /**
     * 사용자가 이미지 파일 업로드 시 로컬에 저장.
     *
     * @param multipartFile the multipart file
     * @param isPublic      the is public
     * @return the image
     * @throws IOException the io exception
     */
    public Image storeFile(MultipartFile multipartFile,String domain, boolean isPublic) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originFilename = multipartFile.getOriginalFilename();
        String storeFilename = UUID.randomUUID() + "." + extractExt(originFilename);

        multipartFile.transferTo(Paths.get(getFullPath(storeFilename)));
        return imageRepository.save(new Image(originFilename, storeFilename, isPublic));
    }

    private String extractExt(String originalFilename) { // 확장자명 가져오기
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
