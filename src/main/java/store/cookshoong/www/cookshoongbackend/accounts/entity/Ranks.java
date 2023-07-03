package store.cookshoong.www.cookshoongbackend.accounts.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "ranks")
public class Ranks implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rank_code", nullable = false)
    private String rankCode;

    @Column(name = "name", nullable = false)
    private String name;

}
