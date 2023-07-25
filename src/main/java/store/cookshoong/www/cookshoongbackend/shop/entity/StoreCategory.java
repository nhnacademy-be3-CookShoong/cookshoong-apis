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
 * 매장 카테고리 엔티티.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "store_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StoreCategory {
    @Id
    @Column(name = "category_code", nullable = false, length = 10)
    private String categoryCode;

    @Column(name = "description", nullable = false, length = 30)
    private String description;


    /**
     * StoreCategory 이름 수정.
     *
     * @param description 카테고리 이름
     */
    public void updateStoreCategory(String description) {
        this.description = description;
    }
}
