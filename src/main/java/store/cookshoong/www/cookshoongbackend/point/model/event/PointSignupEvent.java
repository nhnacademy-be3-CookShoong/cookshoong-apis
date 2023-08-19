package store.cookshoong.www.cookshoongbackend.point.model.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 회원 가입 포인트 제공 이벤트.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Getter
public class PointSignupEvent extends ApplicationEvent {
    private final Long accountId;

    /**
     * Instantiates a new Point signup event.
     *
     * @param source    the source
     * @param accountId the account id
     */
    public PointSignupEvent(Object source, Long accountId) {
        super(source);
        this.accountId = accountId;
    }
}
