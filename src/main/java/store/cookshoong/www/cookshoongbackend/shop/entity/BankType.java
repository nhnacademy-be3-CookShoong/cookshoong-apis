package store.cookshoong.www.cookshoongbackend.shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 은행 타입 엔티티.
 * Bank는 TypeCode, 은행명을 같이 입력해야 Create 되므로 AllArgsConstructor 필요함.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
@Entity
@Getter
@Table(name = "bank_types")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BankType {
    @Id
    @Column(name = "bank_type_code", nullable = false, length = 10)
    private String bankTypeCode;

    @Column(nullable = false, length = 45, unique = true)
    private String description;
}
