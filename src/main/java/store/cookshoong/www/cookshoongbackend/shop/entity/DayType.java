package store.cookshoong.www.cookshoongbackend.shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * 요일 타입 엔티티.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "day_types")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DayType {
    @Id
    @Column(name = "day_code", nullable = false, length = 3)
    private String dayCode;

    @Column(nullable = false, length = 3)
    private String description;

    public DayType(String dayCode, String description) {
        this.dayCode = dayCode;
        this.description = description;
    }

    /**
     * 요일 코드들을 상수로 관리하기 위한 Enum.
     */
    @RequiredArgsConstructor
    public enum Code {
        MON(0), TUE(1), WED(2), THU(3),
        FRI(4), SAT(5), SUN(6);

        private final int dayOrder;

        public int getDayOrder() {
            return dayOrder;
        }
    }
}
