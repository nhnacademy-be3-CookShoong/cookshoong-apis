package store.cookshoong.www.cookshoongbackend.shop.exception.businesshour;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 영업시간 코드 요청에 대한 예외발생.
 *
 * @author papel
 * @since 2023.07.13
 */
public class BusinessHourNotFoundException extends NotFoundException {
    public BusinessHourNotFoundException() {
        super("일치하는 영업시간 기록을 찾을 수 없습니다.");
    }
}
