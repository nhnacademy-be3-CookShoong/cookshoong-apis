package store.cookshoong.www.cookshoongbackend.address.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
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
import store.cookshoong.www.cookshoongbackend.account.entity.Account;

/**
 * 회원과 연결되서 주소를 입력 받아 주소 테이블에 저장되는 Entity.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "account_address")
public class AccountAddress {

    @EmbeddedId
    private Pk pk;

    @MapsId("accountId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @MapsId("addressId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "alias", length = 10, nullable = false)
    private String alias;

    @Column(name = "renewal_at", nullable = false)
    private LocalDateTime renewalAt;

    /**
     * 회원과 주소를 연결하는 pk.
     *
     * @author jeongjewan
     * @since 2023 /07/04
     */
    @Getter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Pk implements Serializable {

        private Long accountId;

        private Long addressId;
    }

    /**
     * 주소를 등록할 때, 주소를 선택할 때.
     * 회원과 주소에 대한 관계에서 갱신날짜를 업데이트해준다.
     * 이렇게 업데이트가 이루어지면
     * 회원과 주소간에 촤근 갱신된 주소를 가지고 와서 매장을 검색하고
     * 주문할 때 최근 생신된 주소를 가지고 와서 보여줄 수 있게 된다.
     *
     * @author jeongjewan
     * @contributer seungyeon
     * @since 2023 /07/18
     */
    public void modifyRenewalAt() {
        this.renewalAt = LocalDateTime.now();
    }
}
