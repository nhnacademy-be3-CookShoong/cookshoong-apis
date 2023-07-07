package store.cookshoong.www.cookshoongbackend.store.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 가맹점 엔티티.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "merchants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Merchant {
    @Id
    @Column(name = "merchant_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;
    @OneToMany(mappedBy = "merchant", orphanRemoval = true)
    private List<Store> store;

    /**
     * 가맹점 생성자.
     *
     * @param name 가맹점 이름.
     */
    public Merchant(String name) {
        this.name = name;
    }

    /**
     * 가맹점 수정을 위한 메소드.
     *
     * @param name 바뀔 이름.
     */
    public void modifyMerchant(String name) {
        this.name = name;
    }
}
