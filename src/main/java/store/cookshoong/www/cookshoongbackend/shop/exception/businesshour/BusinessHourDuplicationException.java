package store.cookshoong.www.cookshoongbackend.shop.exception.businesshour;

/**
 * 운영시간 등록 겹칠 때 exception.
 *
 * @author seungyeon
 * @since 2023.08.22
 */
public class BusinessHourDuplicationException extends RuntimeException {
    public BusinessHourDuplicationException() {
        super("해당 운영시간은 이미 등록된 운영시간과 겹칩니다.");
    }
}
