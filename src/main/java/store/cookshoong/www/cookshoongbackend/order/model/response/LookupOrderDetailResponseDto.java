package store.cookshoong.www.cookshoongbackend.order.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * 포인트 계산을 위한 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Getter
public class LookupOrderDetailResponseDto {
    private final int nowCost;

    private final int count;

    @Setter
    private Double earningRate;

    /**
     * Instantiates a new Lookup order menu earning rate response dto.
     *
     * @param nowCost     the now cost
     * @param count       the count
     * @param earningRate the earning rate
     */
    @QueryProjection
    public LookupOrderDetailResponseDto(int nowCost, int count, BigDecimal earningRate) {
        this.nowCost = nowCost;
        this.count = count;

        if (Objects.nonNull(earningRate)) {
            this.earningRate = earningRate.doubleValue();
        }
    }

    public int getTotalCost() {
        return getNowCost() * getCount();
    }
}
