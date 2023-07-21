package store.cookshoong.www.cookshoongbackend.search.model;

import lombok.Getter;

/**
 * 매장 도큐먼트 응답 Dto.
 *
 * @author papel
 * @since 2023.07.21
 */
@Getter
public class StoreDocumentResponseDto {
    private Long id;

    private String name;

    private String description;

    public StoreDocumentResponseDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static StoreDocumentResponseDto from(StoreDocument storeDocument) {
        return new StoreDocumentResponseDto(
            storeDocument.getId(),
            storeDocument.getName(),
            storeDocument.getDescription());
    }
}
