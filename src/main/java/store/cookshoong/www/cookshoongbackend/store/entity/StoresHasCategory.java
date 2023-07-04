package store.cookshoong.www.cookshoongbackend.store.entity;

import javax.persistence.*;

import lombok.*;

import java.io.Serializable;

/**
 * 매장-카테고리 엔티티.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "stores_has_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoresHasCategory {
    //TODO 3. Pk 이름 규칙 어떻게할지
    @EmbeddedId
    private Pk pk;

    @MapsId("storeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @MapsId("categoryCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_code", nullable = false)
    private StoreCategory categoryCode;

    /**
     * 매장_카테고리 엔티티 복합키.
     *
     */
    @Embeddable
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk implements Serializable {
        private Long storeId;
        private String categoryCode;
    }
}
