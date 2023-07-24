package store.cookshoong.www.cookshoongbackend.menu_order.exception.menu;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 메뉴 코드 요청에 대한 예외.
 *
 * @author papel (윤동현)
 * @since 2023.07.13
 */
public class MenuNotFoundException extends NotFoundException {
    private static final String ERRORMESSAGE = "해당 이름의 메뉴가 없습니다.";
    public MenuNotFoundException() {
        super(ERRORMESSAGE);
    }
}
