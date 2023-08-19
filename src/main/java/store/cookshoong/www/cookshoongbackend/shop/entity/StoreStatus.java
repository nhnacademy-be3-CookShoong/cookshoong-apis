package store.cookshoong.www.cookshoongbackend.shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 상태 엔티티.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "store_status")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StoreStatus {
    @Id
    @Column(name = "store_status_code", nullable = false, length = 10)
    private String code;

    @Column(name = "description", length = 30)
    private String description;

    /**
     * 매장 상태코드.
     */
    public enum StoreStatusCode {
        /**
         * 매장 오픈.
         */
        OPEN,
        /**
         * 매장 강제 휴식/문닫기.
         */
        CLOSE,
        /**
         * 매장 영엄중단(폐업).
         */
        OUTED,
        /**
         * 매장 휴식(일정에 맞춘).
         */
        BREAK_TIME
    }
}
