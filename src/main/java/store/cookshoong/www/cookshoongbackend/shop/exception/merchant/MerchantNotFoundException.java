package store.cookshoong.www.cookshoongbackend.shop.exception.merchant;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 존재하지 않는 가맹점 예외.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
public class MerchantNotFoundException extends NotFoundException {
    public MerchantNotFoundException() {
        super("가맹점");
    }
}
