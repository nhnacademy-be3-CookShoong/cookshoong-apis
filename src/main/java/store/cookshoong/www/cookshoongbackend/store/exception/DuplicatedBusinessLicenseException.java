package store.cookshoong.www.cookshoongbackend.store.exception;

/**
 * 사업자등록번 중복 시 발생하는 예외.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
public class DuplicatedBusinessLicenseException extends RuntimeException {
    public DuplicatedBusinessLicenseException(String businessLicenseNumber) {
        super(businessLicenseNumber + "은 이미 등록되어 있습니다.");
    }
}
