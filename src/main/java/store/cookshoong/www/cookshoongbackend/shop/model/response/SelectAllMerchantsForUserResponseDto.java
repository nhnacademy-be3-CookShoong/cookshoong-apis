package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사업자 : 매장 등록에서 가맹점 등록시 select box 구현을 위하여 사용할 dto.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.12
 */
@Getter
public class SelectAllMerchantsForUserResponseDto {
    private final Long id;
    private final String name;

    /**
     * 가맹점 아이디와 이름을 담는 response dto.
     *
     * @param id   the id
     * @param name the name
     */
    @QueryProjection
    public SelectAllMerchantsForUserResponseDto(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
