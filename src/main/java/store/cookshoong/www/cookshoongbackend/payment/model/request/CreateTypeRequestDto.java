package store.cookshoong.www.cookshoongbackend.payment.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결제 타입과 환불 타입에 name 에 해당하는 Request Dto.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@Getter
@AllArgsConstructor
public class CreateTypeRequestDto {

    private String name;
}
