package store.cookshoong.www.cookshoongbackend.order.entity;

import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 상태 엔티티.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.17
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "order_status")
public class OrderStatus {
    @Id
    @Column(name = "order_status_code", nullable = false, length = 10)
    private String code;

    @Column(name = "description", nullable = false, length = 40)
    private String description;

    /**
     * 저장된 statusCode.
     */
    public enum StatusCode {
        CREATE, PAY, CANCEL, COOKING, FOOD_OUT, ORD_FLOOD, DELIVER, COMPLETE, PARTIAL;

        /**
         * Gets status code string.
         *
         * @param statusCodes the status codes
         * @return the status code string
         */
        public static Set<String> getStatusCodeString(Set<StatusCode> statusCodes) {
            return statusCodes.stream()
                .map(StatusCode::name)
                .collect(Collectors.toSet());
        }
    }
}
