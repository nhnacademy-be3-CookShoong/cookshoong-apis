package store.cookshoong.www.cookshoongbackend.menu.exception.menu;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 메뉴 상태 코드 요청에 대한 예외발생.
 *
 * @author papel
 * @since 2023.07.13
 */
public class MenuStatusNotFoundException extends NotFoundException {
    public MenuStatusNotFoundException() {
        super("해당 이름의 메뉴 상태가 없습니다.");
    }
}
