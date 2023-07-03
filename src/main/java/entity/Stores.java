package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@Data
@Entity
@Table(name = "stores")
public class Stores implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "store_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "bank_type_code", nullable = false)
    private String bankTypeCode;

    @Column(name = "store_status_code", nullable = false)
    private String storeStatusCode;

    @Column(name = "business_license", nullable = false)
    private String businessLicense;

    @Column(name = "business_license_number", nullable = false)
    private String businessLicenseNumber;

    @Column(name = "representative_name", nullable = false)
    private String representativeName;

    @Column(name = "opening_date", nullable = false)
    private Date openingDate;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "default_earning_rate", nullable = false)
    private BigDecimal defaultEarningRate;

    @Column(name = "image")
    private String image;

    @Column(name = "bank_account_number", nullable = false)
    private String bankAccountNumber;

    @Column(name = "description")
    private String description;

}
