package store.cookshoong.www.cookshoongbackend.address.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 회원이 주소를 등록할 때.
 * 회원과 주소관계에서 별칭을 저장하고 주소로 가서 메인, 상세주소, 위도, 경도를 생성
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@AllArgsConstructor
public class CreateAccountAddressRequestDto {

    private String alias;

    private String mainPlace;

    private String detailPlace;

    private BigDecimal latitude;

    private BigDecimal longitude;
}
