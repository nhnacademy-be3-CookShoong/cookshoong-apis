package store.cookshoong.www.cookshoongbackend.store.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 없는 휴업일 탐색 요청시 발생하는 예외
 *
 * @author papel
 * @since 2023.07.07
 */
public class HolidayNotFoundException extends NotFoundException {
    public HolidayNotFoundException(Long holidayId) {
        super(holidayId + "와 일치하는 휴업일 기록을 찾을 수 없습니다.");
    }
}
