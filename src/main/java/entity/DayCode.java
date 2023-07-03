package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "day_code")
public class DayCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "day_code", nullable = false)
    private String dayCode;

    @Column(name = "description", nullable = false)
    private String description;

}
