package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "bank_types")
public class BankTypes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "bank_type_code", nullable = false)
    private String bankTypeCode;

    @Column(name = "description")
    private String description;

}
