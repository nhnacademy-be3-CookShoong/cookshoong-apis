package store.cookshoong.www.cookshoongbackend.order.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Getter;

/**
 * 주문 메뉴 정보를 확인하는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@Getter
public class LookupOrderDetailMenuResponseDto {
    private final Long orderDetailId;
    private final String menuName;
    private final int cookingTime;
    private final int count;
    private final int cost;

    private List<LookupOrderDetailMenuOptionResponseDto> selectOrderDetailMenuOptions;
    private int totalCost;

    /**
     * Instantiates a new Select order detail dto.
     *
     * @param orderDetailId the order detail id
     * @param menuName      the menu name
     * @param cookingTime   the cooking time
     * @param count         the count
     * @param cost          the cost
     */
    @QueryProjection
    public LookupOrderDetailMenuResponseDto(Long orderDetailId, String menuName, int cookingTime, int count, int cost) {
        this.orderDetailId = orderDetailId;
        this.menuName = menuName;
        this.cookingTime = cookingTime;
        this.count = count;
        this.cost = cost;
    }

    /**
     * Sets select order detail menu options.
     *
     * @param options the options
     */
    public void updateSelectOrderDetailMenuOptions(List<LookupOrderDetailMenuOptionResponseDto> options) {
        this.selectOrderDetailMenuOptions = options;

        int optionPrice = options.stream()
            .mapToInt(LookupOrderDetailMenuOptionResponseDto::getPrice)
            .sum();

        this.totalCost = this.count * (this.cost + optionPrice);
    }
}
