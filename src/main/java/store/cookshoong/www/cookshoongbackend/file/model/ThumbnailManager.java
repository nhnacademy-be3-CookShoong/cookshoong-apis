package store.cookshoong.www.cookshoongbackend.file.model;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 썸네일 폴더 관리.
 *
 * @author seungyeon
 * @since 2023.08.19
 */
@Component
public class ThumbnailManager {
    private final Map<String, String> thumbnailTree = new HashMap<>();

    /**
     * 해당 도메인은 thumbnail 폴더가 포함.
     */
    public ThumbnailManager() {
        thumbnailTree.put(FileDomain.STORE_IMAGE.getVariable(), FileDomain.STORE_IMAGE_THUMB.getVariable());
        thumbnailTree.put(FileDomain.MENU_IMAGE.getVariable(), FileDomain.MENU_IMAGE_THUMB.getVariable());
        thumbnailTree.put(FileDomain.REVIEW.getVariable(), FileDomain.REVIEW_THUMB.getVariable());
    }

    /**
     * 썸네일 폴더가 생성되어있는지, true : 썸네일 폴더 있음.
     *
     * @param fileDomain the file domain
     * @return the boolean
     */
    public boolean isImageContainsThumb(String fileDomain) {
        return thumbnailTree.containsKey(fileDomain);
    }

    /**
     * 썸네일 폴더 이름 반환.
     *
     * @param fileDomain the file domain
     * @return the thumbnail domain
     */
    public String getThumbnailDomain(String fileDomain) {
        return thumbnailTree.get(fileDomain);
    }
}
