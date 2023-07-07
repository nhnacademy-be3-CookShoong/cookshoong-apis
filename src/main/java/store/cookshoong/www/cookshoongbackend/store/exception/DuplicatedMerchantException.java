package store.cookshoong.www.cookshoongbackend.store.exception;

/**
 * 중복된 가맹점 등록에 대한 예외.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
public class DuplicatedMerchantException extends RuntimeException {
    public DuplicatedMerchantException(String name) {
        super(name + "는 이미 등록된 가맹점입니다.");
    }
}
