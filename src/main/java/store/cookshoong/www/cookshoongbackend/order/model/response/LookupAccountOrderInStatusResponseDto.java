package store.cookshoong.www.cookshoongbackend.order.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.Objects;
import lombok.Getter;

/**
 * 유저 주문 기록 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.13
 */
@Getter
public class LookupAccountOrderInStatusResponseDto {
    private final LookupOrderInStatusResponseDto lookupOrderInStatusResponseDto;
    private final String storeName;
    private final Integer couponDiscountAmount;
    private final Integer pointDiscountAmount;
    private final Integer deliveryCost;
    private final boolean writableReview;
    private int totalOrderPrice;

    /**
     * Instantiates a new Lookup account order in status response dto.
     *
     * @param lookupOrderInStatusResponseDto the lookup order in status response dto
     * @param storeName                      the store name
     * @param couponDiscountAmount           the coupon discount amount
     * @param pointDiscountAmount            the point discount amount
     * @param deliveryCost                   the delivery cost
     */
    @QueryProjection
    public LookupAccountOrderInStatusResponseDto(LookupOrderInStatusResponseDto lookupOrderInStatusResponseDto,
                                                 String storeName, Integer couponDiscountAmount,
                                                 Integer pointDiscountAmount, Integer deliveryCost,
                                                 Long reviewId) {
        this.lookupOrderInStatusResponseDto = lookupOrderInStatusResponseDto;
        this.storeName = storeName;
        this.couponDiscountAmount = couponDiscountAmount;
        this.pointDiscountAmount = pointDiscountAmount;
        this.deliveryCost = deliveryCost;
        this.writableReview = validWritable(reviewId);
    }

    /**
     * Update total order price.
     */
    public void updateTotalOrderPrice() {
        this.totalOrderPrice = lookupOrderInStatusResponseDto.getSelectOrderDetails()
            .stream()
            .mapToInt(LookupOrderDetailMenuResponseDto::getTotalCost)
            .sum();
    }

    private boolean validWritable(Long reviewId) {
        return Objects.isNull(reviewId);
    }
}
