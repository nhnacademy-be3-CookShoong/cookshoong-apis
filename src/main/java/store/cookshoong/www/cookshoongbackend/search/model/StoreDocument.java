package store.cookshoong.www.cookshoongbackend.search.model;

import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 엘라스틱 서치 매장 도큐먼트 엔티티.
 *
 * @author papel
 * @since 2023.07.21
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "store")
public class StoreDocument {
    @Id
    private Long id;
    private String name;
    private String description;
    private String saved_name;

    public static StoreDocument from(StoreDocumentRequestDto storeDocumentRequestDto) {
        return StoreDocument.builder()
            .id(storeDocumentRequestDto.getId())
            .name(storeDocumentRequestDto.getName())
            .description(storeDocumentRequestDto.getDescription())
            .saved_name(storeDocumentRequestDto.getSaved_name())
            .build();
    }
}
