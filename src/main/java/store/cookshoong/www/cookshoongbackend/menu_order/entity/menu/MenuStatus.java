package store.cookshoong.www.cookshoongbackend.menu_order.entity.menu;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 상태 엔티티.
 *
 * @author papel (윤동현)
 * @since 2023.07.11
 */
@Getter
@Entity
@Table(name = "menu_status")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuStatus {
    @Id
    @Column(name = "menu_status_code", nullable = false, length = 10)
    private String menuStatusCode;

    @Column(name = "description", nullable = false, length = 10)
    private String description;

    public enum MenuStatusCode {
        OPEN, CLOSE, OUTED
    }
}
