package store.cookshoong.www.cookshoongbackend.menu_order.exception.option;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 옵션 그룹 코드 요청에 대한 예외.
 *
 * @author papel
 * @since 2023.07.13
 */
public class OptionGroupNotFoundException extends NotFoundException {
    private static final String ERRORMESSAGE = "해당 이름의 옵션 그룹이 없습니다.";
    public OptionGroupNotFoundException() {
        super(ERRORMESSAGE);
    }
}
