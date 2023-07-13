package store.cookshoong.www.cookshoongbackend.shop.entity;

import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 영업시간 엔티티.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
@Entity
@Getter
@Table(name = "business_hours")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_hours_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "day_code", nullable = false)
    private DayType dayCode;

    @Column(name = "open_hour", nullable = false)
    private LocalTime openHour;

    @Column(name = "close_hour", nullable = false)
    private LocalTime closeHour;

    /**
     * 영업시간 생성자.
     *
     * @param store 매장
     * @param dayCode 요일
     * @param openHour 영업시작시간
     * @param closeHour 영업종료시간
     */
    public BusinessHour(Store store, DayType dayCode, LocalTime openHour, LocalTime closeHour) {
        this.store = store;
        this.dayCode = dayCode;
        this.openHour = openHour;
        this.closeHour = closeHour;
    }
}
