package store.cookshoong.www.cookshoongbackend.shop.exception.store;

/**
 * 해당 사용자에게 접근 권한이 없습니다.
 *
 * @author seungyeon
 * @since 2023.07.13
 */
public class UserAccessDeniedException extends RuntimeException {
    public UserAccessDeniedException(String message){
        super(message);
    }
}
