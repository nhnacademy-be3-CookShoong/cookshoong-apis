package store.cookshoong.www.cookshoongbackend.shop.exception.holiday;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 휴업일 코드 요청에 대한 예외발생.
 *
 * @author papel
 * @since 2023.07.13
 */
public class HolidayNotFoundException extends NotFoundException {
    public HolidayNotFoundException() {
        super("일치하는 휴업일 기록을 찾을 수 없습니다.");
    }
}
