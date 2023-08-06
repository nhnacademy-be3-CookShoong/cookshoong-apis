package store.cookshoong.www.cookshoongbackend.payment.model.request;

import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 환불에 대해 알아야할 정보.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateRefundDto {

    @NotNull
    private UUID chargeCode;
    @NotBlank
    private String refundAt;
    @NotNull
    private Integer refundAmount;
    @NotBlank
    private String refundType;
}
