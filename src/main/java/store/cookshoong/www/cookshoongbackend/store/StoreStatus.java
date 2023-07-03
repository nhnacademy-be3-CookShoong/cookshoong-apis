package store.cookshoong.www.cookshoongbackend.store;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "store_status")
public class StoreStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "store_status_code", nullable = false)
    private String storeStatusCode;

    @Column(name = "description")
    private String description;

}
