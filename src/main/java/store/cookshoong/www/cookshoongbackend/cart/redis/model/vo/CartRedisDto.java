package store.cookshoong.www.cookshoongbackend.cart.redis.model.vo;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 장바구니에 저장될 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartRedisDto {
    @NotNull
    private Long accountId;
    @NotNull
    private Long storeId;
    @NotBlank
    private String storeName;
    private CartMenuDto menu;
    private List<CartOptionDto> options;
    private Long createTimeMillis;
    private String hashKey;
    private int count = 1;

    /**
     * 메뉴와 옵션을 조합해서 hashKey 를 생성하는 메서드.
     *
     * @return      메뉴 + 오셥s -> hashKey 반환
     */
    public String generateUniqueHashKey() {
        // menuId와 optionDtos에서 optionId들을 모아서 하나의 문자열로 합칩니다.
        String optionIdsString = options.stream()
            .map(cartOptionDto -> cartOptionDto.getOptionId().toString())
            .collect(Collectors.joining(","));

        // menuId와 optionIdsString을 조합하여 유일한 hashKey를 생성합니다.
        return menu.getMenuId() + ":" + optionIdsString;
    }

    public void createTimeMillis() {
        this.createTimeMillis = System.currentTimeMillis();
    }

    public void refreshcreateTimeMillis(Long updateAt) {
        this.createTimeMillis = updateAt;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public void setRefreshHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public void incrementCount() {
        count++;
    }

    public void decrementCount() {
        count--;
    }
}
