package store.cookshoong.www.cookshoongbackend.payment.model.request;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import store.cookshoong.www.cookshoongbackend.common.util.RegularExpressions;
import store.cookshoong.www.cookshoongbackend.common.util.ValidationFailureMessages;

/**
 * 결제 타입과 환불 타입에 name 을 생성하는 Request Dto.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
public class CreateTypeRequestDto {

    @NotBlank
    @Length(min = 1, max = 30)
    @Pattern(regexp = RegularExpressions.LETTER_ONLY_WITH_BLANK, message = ValidationFailureMessages.LETTER_ONLY)
    private String name;
}

