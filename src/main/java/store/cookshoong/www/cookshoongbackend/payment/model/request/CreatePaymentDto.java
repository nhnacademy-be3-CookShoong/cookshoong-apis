package store.cookshoong.www.cookshoongbackend.payment.model.request;

import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 결제에 대해 알아야할 정보.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePaymentDto {

    @NotNull
    private UUID orderId;
    @NotBlank
    private String paymentType;
    @NotBlank
    private String chargedAt;
    @NotNull
    private Integer chargedAmount;
    @NotBlank
    private String paymentKey;
    @NotBlank
    private String cartKey;

    private UUID couponCode;

    private Integer point;
}
