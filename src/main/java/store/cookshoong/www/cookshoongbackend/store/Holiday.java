package store.cookshoong.www.cookshoongbackend.store;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Data
@Entity
@Table(name = "holiday")
public class Holiday implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "holiday_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long holidayId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "holiday_date", nullable = false)
    private Date holidayDate;

}
