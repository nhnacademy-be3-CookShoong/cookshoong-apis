package store.cookshoong.www.cookshoongbackend.address.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

/**
 * 회원과 연결되서 주소를 입력 받아 주소 테이블에 저장되는 Entity.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "account_address")
public class AccountAddress {

    @EmbeddedId
    private Pk pk;

    @MapsId("addressId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "alias", nullable = false, length = 10)
    @ColumnDefault("기본")
    private String alias;

    /**
     * 회원과 주소를 연결하는 pk.
     *
     * @author jeongjewan
     * @since 2023/07/04
     */
    @Getter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk implements Serializable {

        @Column(name = "account_id")
        private Long accountId;

        @Column(name = "address_id")
        private Long addressId;
    }


}
