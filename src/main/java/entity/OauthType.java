package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "oauth_type")
public class OauthType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "oauth_type_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oauthTypeId;

    @Column(name = "provider", nullable = false)
    private String provider;

}
