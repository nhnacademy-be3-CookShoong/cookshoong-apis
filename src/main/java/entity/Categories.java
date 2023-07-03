package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "categories")
public class Categories implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "category_code", nullable = false)
    private String categoryCode;

    @Column(name = "description", nullable = false)
    private String description;

}
