package store.cookshoong.www.cookshoongbackend.store.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 은행 타입 엔티티.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
@Entity
@Getter
@Table(name = "bank_types")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankType {
    @Id
    @Column(name = "bank_type_code", nullable = false, length = 10)
    private String bankTypeCode;

    @Column(nullable = false, length = 45)
    private String description;
}
