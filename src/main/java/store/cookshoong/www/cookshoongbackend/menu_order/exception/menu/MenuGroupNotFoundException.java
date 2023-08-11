package store.cookshoong.www.cookshoongbackend.menu_order.exception.menu;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 메뉴 그룹 코드 요청에 대한 예외.
 *
 * @author papel (윤동현)
 * @since 2023.07.13
 */
public class MenuGroupNotFoundException extends NotFoundException {
    private static final String ERRORMESSAGE = "메뉴 그룹";
    public MenuGroupNotFoundException() {
        super(ERRORMESSAGE);
    }
}
