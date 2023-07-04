package store.cookshoong.www.cookshoongbackend.store.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
//TODO 2. *로 되는데 어떻게 할지
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;

/**
 * 매장 엔티티.
 *
 * @author seungyeon
 * @since 2023.07.04
 */

@Getter
@Entity
@Table(name = "stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    //TODO 1.Account MayToOne 걸기

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_type_code", nullable = false)
    private BankType bankTypeCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_status_code", nullable = false)
    private StoreStatus storeStatusCode;

    @Column(name = "business_license", nullable = false, length = 40)
    private String businessLicense;

    @Column(name = "business_license_number", nullable = false, length = 10)
    private String businessLicenseNumber;

    @Column(name = "representative_name", nullable = false, length = 30)
    private String representativeName;

    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 12)
    private String phoneNumber;

    @Column(name = "default_earning_rate", nullable = false, precision = 4, scale = 1)
    private BigDecimal defaultEarningRate;

    @Lob
    private String description;

    @Column(length = 40)
    private String image;

    @Column(name = "bank_account_number", nullable = false, length = 20)
    private String bankAccountNumber;
}
