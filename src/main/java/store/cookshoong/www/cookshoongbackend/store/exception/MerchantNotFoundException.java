package store.cookshoong.www.cookshoongbackend.store.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 존재하지 않는 가맹점 예외.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
public class MerchantNotFoundException extends NotFoundException {
    public MerchantNotFoundException(Long merchantId) {
        super(merchantId + " 가맹점은 없습니다.");
    }
}
