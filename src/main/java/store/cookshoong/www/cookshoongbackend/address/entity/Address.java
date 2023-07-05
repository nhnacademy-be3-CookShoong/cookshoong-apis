package store.cookshoong.www.cookshoongbackend.address.entity;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주소 정보에 해당하는 엔티티.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "main_place", length = 80, nullable = false)
    private String mainPlace;

    @Column(name = "detail_place", length = 80)
    private String detailPlace;

    @Column(name = "latitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal longitude;

    /**
     * Address 생성자.
     *
     * @param mainPlace mainPlace
     * @param detailPlace detailPlace
     * @param latitude latitude
     * @param longitude longitude
     */
    public Address(String mainPlace, String detailPlace, BigDecimal latitude, BigDecimal longitude) {
        this.mainPlace = mainPlace;
        this.detailPlace = detailPlace;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
