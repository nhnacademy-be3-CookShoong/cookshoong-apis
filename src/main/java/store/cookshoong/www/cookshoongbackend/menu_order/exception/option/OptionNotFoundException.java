package store.cookshoong.www.cookshoongbackend.menu_order.exception.option;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 옵션 코드 요청에 대한 예외.
 *
 * @author papel (윤동현)
 * @since 2023.07.13
 */
public class OptionNotFoundException extends NotFoundException {
    private static final String ERRORMESSAGE = "옵션";
    public OptionNotFoundException() {
        super(ERRORMESSAGE);
    }
}
