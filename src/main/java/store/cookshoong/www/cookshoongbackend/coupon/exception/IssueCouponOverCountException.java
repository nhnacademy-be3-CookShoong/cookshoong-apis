package store.cookshoong.www.cookshoongbackend.coupon.exception;

/**
 * 쿠폰 생성 가능 개수를 초과하였을 때 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.19
 */
public class IssueCouponOverCountException extends RuntimeException {
    public IssueCouponOverCountException(int issueCouponLimitCount) {
        super("쿠폰 생성 가능 개수를 초과하였습니다. 남은 수량 + 생성 수량이 " + issueCouponLimitCount + "개를 넘어선 안 됩니다.");
    }
}
