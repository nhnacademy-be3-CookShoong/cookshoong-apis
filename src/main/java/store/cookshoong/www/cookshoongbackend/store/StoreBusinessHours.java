package store.cookshoong.www.cookshoongbackend.store;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Data
@Entity
@Table(name = "store_business_hours")
public class StoreBusinessHours implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_hours_id", nullable = false)
    private Long businessHoursId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "day_code", nullable = false)
    private String dayCode;

    @Column(name = "open_hour", nullable = false)
    private Time openHour;

    @Column(name = "close_hour", nullable = false)
    private Time closeHour;

}
