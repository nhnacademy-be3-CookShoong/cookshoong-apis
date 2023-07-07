package store.cookshoong.www.cookshoongbackend.store.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 없는 회원 권한 요청시 발생하는 예외.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public class SelectHolidayNotFoundException extends NotFoundException {
    public SelectHolidayNotFoundException(Long holidayId) {
        super(holidayId + "와 일치하는 휴업일 기록을 찾을 수 없습니다.");
    }
}
