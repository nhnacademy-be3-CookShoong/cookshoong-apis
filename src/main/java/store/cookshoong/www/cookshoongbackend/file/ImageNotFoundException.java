package store.cookshoong.www.cookshoongbackend.file;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 존재하지 않는 이미지에 대한 예외.
 *
 * @author seungyeon (유승연)
 * @since 2023.08.08
 */
public class ImageNotFoundException extends NotFoundException {
    public ImageNotFoundException() {
        super("이미지");
    }
}
