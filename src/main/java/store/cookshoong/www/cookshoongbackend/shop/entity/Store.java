package store.cookshoong.www.cookshoongbackend.shop.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;

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


    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_type_code", nullable = false)
    private BankType bankTypeCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_status_code", nullable = false)
    private StoreStatus storeStatusCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_license_image_id", nullable = false)
    private Image businessLicense;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_image_id", nullable = false)
    private Image storeImage;

    @Column(name = "bank_account_number", nullable = false, length = 20)
    private String bankAccountNumber;

    @OneToMany(mappedBy = "store", cascade = CascadeType.PERSIST)
    private final Set<StoresHasCategory> storesHasCategories = new HashSet<>();

    /**
     * 주소 생성자.
     *
     * @param merchant              가맹점
     * @param account               회원
     * @param bankTypeCode          은행타입
     * @param storeStatus           가게 상태
     * @param businessLicenseNumber 사업자등록번호
     * @param representativeName    대표자 이름
     * @param openingDate           개업일자
     * @param name                  상호명
     * @param phoneNumber           가게 번호
     * @param defaultEarningRate    매장 별 기본 적립률
     * @param description           매장 설명
     * @param bankAccountNumber     은행 계좌 번호
     */
    public Store(Merchant merchant, Account account, BankType bankTypeCode, StoreStatus storeStatus,
                 Image businessLicense, String businessLicenseNumber, String representativeName,
                 LocalDate openingDate, String name, String phoneNumber, BigDecimal defaultEarningRate,
                 String description, Image storeImage, String bankAccountNumber) {
        this.merchant = merchant;
        this.account = account;
        this.bankTypeCode = bankTypeCode;
        this.storeStatusCode = storeStatus;
        this.businessLicense = businessLicense;
        this.businessLicenseNumber = businessLicenseNumber;
        this.representativeName = representativeName;
        this.openingDate = openingDate;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.defaultEarningRate = defaultEarningRate;
        this.description = description;
        this.storeImage = storeImage;
        this.bankAccountNumber = bankAccountNumber;
    }

    /**
     * 주소 등록을 위해 사용.
     *
     * @param address 주소 값
     */
    public void modifyAddress(Address address) {
        this.address = address;
    }

    /**
     * 매장 정보 수정.
     *
     * @param merchant              the merchant
     * @param account               the account
     * @param bankTypeCode          the bank type code
     * @param storeStatus           the store status
     * @param businessLicenseNumber the business license number
     * @param representativeName    the representative name
     * @param openingDate           the opening date
     * @param name                  the name
     * @param phoneNumber           the phone number
     * @param defaultEarningRate    the default earning rate
     * @param description           the description
     * @param storeImage            the store image
     * @param bankAccountNumber     the bank account number
     */
    public void modifyStoreInfo(Merchant merchant, Account account, BankType bankTypeCode, StoreStatus storeStatus,
                                String businessLicenseNumber, String representativeName,
                                LocalDate openingDate, String name, String phoneNumber, BigDecimal defaultEarningRate,
                                String description, Image storeImage, String bankAccountNumber) {
        this.merchant = merchant;
        this.account = account;
        this.bankTypeCode = bankTypeCode;
        this.storeStatusCode = storeStatus;
        this.businessLicenseNumber = businessLicenseNumber;
        this.representativeName = representativeName;
        this.openingDate = openingDate;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.defaultEarningRate = defaultEarningRate;
        this.description = description;
        this.storeImage = storeImage;
        this.bankAccountNumber = bankAccountNumber;
    }

    /**
     * 매장 카테고리 초기화.
     *
     * @author seungyeon
     * @since 2023.07.18
     */
    public void initStoreCategories() {
        this.storesHasCategories.clear();
    }

    /**
     * 매장의 상태코드 변경.
     *
     * @param storeStatusCode 매장 상태 코드(OPEN, CLOSE, OUTED)
     */
    public void modifyStoreStatus(StoreStatus storeStatusCode) {
        this.storeStatusCode = storeStatusCode;
    }
}
