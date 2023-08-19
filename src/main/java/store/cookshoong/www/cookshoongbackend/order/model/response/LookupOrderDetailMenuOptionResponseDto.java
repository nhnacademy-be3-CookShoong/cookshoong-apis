package store.cookshoong.www.cookshoongbackend.order.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 주문 메뉴의 옵션을 확인하는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@Getter
public class LookupOrderDetailMenuOptionResponseDto {
    private final String optionName;
    private final int price;

    @QueryProjection
    public LookupOrderDetailMenuOptionResponseDto(String optionName, int price) {
        this.optionName = optionName;
        this.price = price;
    }
}
