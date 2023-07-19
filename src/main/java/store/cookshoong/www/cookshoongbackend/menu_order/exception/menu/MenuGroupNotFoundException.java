package store.cookshoong.www.cookshoongbackend.menu_order.exception.menu;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 메뉴 그룹 코드 요청에 대한 예외.
 *
 * @author papel
 * @since 2023.07.13
 */
public class MenuGroupNotFoundException extends NotFoundException {
    private static final String ERRORMESSAGE = "해당 이름의 메뉴 그룹이 없습니다.";
    public MenuGroupNotFoundException() {
        super(ERRORMESSAGE);
    }
}
