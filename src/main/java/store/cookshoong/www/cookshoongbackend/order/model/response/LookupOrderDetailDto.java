package store.cookshoong.www.cookshoongbackend.order.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문 메뉴 정보를 확인하는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@Getter
public class LookupOrderDetailDto {
    private final Long orderDetailId;
    private final String menuName;
    private final int cookingTime;
    private final int count;
    @Setter
    private List<LookupOrderDetailMenuOptionDto> selectOrderDetailMenuOptions;

    /**
     * Instantiates a new Select order detail dto.
     *
     * @param menuName    the menu name
     * @param cookingTime the cooking time
     * @param count       the count
     */
    @QueryProjection
    public LookupOrderDetailDto(Long orderDetailId, String menuName, int cookingTime, int count) {
        this.orderDetailId = orderDetailId;
        this.menuName = menuName;
        this.cookingTime = cookingTime;
        this.count = count;
    }
}
