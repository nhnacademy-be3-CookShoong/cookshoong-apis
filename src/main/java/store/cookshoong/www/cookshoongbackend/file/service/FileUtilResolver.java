package store.cookshoong.www.cookshoongbackend.file.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;

/**
 * 파일 저장 경로에 따라 사용하는 Storage 방식 결정.
 *
 * @author seungyeon
 * @since 2023.08.06
 */
@Component
@RequiredArgsConstructor
public class FileUtilResolver {
    private final List<FileUtils> fileUtilsList;

    /**
     * 파일 저장 경로에 따라 사용하는 Storage 방식이 다름.
     * 현재 존재하는 방식은 Local, ObjectStorage 2가지 방식.
     *
     * @param locationType the location type
     * @return the file util
     */
    public FileUtils getFileService(String locationType) {
        return fileUtilsList.stream()
            .filter(fileUtils -> fileUtils.matchStorageType(locationType))
            .findAny()
            .orElse(fileUtilsList.get(0));
    }
}
