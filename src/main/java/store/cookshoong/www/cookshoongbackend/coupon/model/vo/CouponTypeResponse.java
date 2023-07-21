package store.cookshoong.www.cookshoongbackend.coupon.model.vo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 쿠폰 타입 객체를 묶기 위한 interface.
 *
 * @author eora21
 * @since 2023.07.06
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CouponTypeCashVo.class, name = "cash"),
    @JsonSubTypes.Type(value = CouponTypePercentVo.class, name = "percent")
})
public interface CouponTypeResponse {

}
