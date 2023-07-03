package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "authorities")
public class Authorities implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "authority_code", nullable = false)
    private String authorityCode;

    @Column(name = "description", nullable = false)
    private String description;

}
