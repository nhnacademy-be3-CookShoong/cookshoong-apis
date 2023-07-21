package store.cookshoong.www.cookshoongbackend.search.model;

import java.util.List;
import lombok.Getter;

/**
 * 매장 전체 도큐먼트 생성 Dto.
 *
 * @author papel
 * @since 2023.07.21
 */
@Getter
public class StoreDocumentRequestAllDto {
    private List<StoreDocumentRequestDto> storeDocumentRequestDtoList;
}
