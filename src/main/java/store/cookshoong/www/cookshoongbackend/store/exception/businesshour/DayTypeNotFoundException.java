package store.cookshoong.www.cookshoongbackend.store.exception.businesshour;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 요일 코드 요청에 대한 예외발생.
 *
 * @author papel
 * @since 2023.07.10
 */
public class DayTypeNotFoundException extends NotFoundException {
    public DayTypeNotFoundException() {
        super("요일을 선택해야 합니다.");

    }
}
