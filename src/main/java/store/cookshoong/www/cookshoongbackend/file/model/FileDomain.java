package store.cookshoong.www.cookshoongbackend.file.model;

import lombok.RequiredArgsConstructor;

/**
 * Object Storage, Local 등 파일을 관리하는 파일명 Enum 으로 관리.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.26
 */
@RequiredArgsConstructor
public enum FileDomain {
    /**
     * 사업자등록증 관리하는 폴더명.
     */
    BUSINESS_INFO_IMAGE("businessInfoImage"),
    /**
     * 매장 이미지를 관리하는 폴더명.
     */
    STORE_IMAGE("storeImage"),
    STORE_IMAGE_THUMB("thumb/storeImage"),
    /**
     * 메뉴 이미지를 관리하는 폴더명.
     */
    MENU_IMAGE("menuImage"),
    MENU_IMAGE_THUMB("thumb/menuImage"),
    /**
     * 로컬에서 파일 관리하는 폴더명.
     */
    FILES("files"),
    REVIEW("reviewImage"),
    REVIEW_THUMB("thumb/reviewImage");
    private final String variable;

    /**
     * 폴더명.
     *
     * @return the variable
     */
    public String getVariable() {
        return variable;
    }
}
