package store.cookshoong.www.cookshoongbackend.shop.exception.banktype;

/**
 * 은행 중복될 시 예외.
 *
 * @author seungyeon
 * @since 2023.07.09
 */
public class DuplicatedBankException extends RuntimeException {
    public DuplicatedBankException(String bankName) {
        super(bankName + "은 이미 등록되어 있습니다.");
    }
}
