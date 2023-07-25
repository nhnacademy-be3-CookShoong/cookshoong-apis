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
    private int count;

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

    /**
     * 장바구니가 만들어진 시간을 생성 -> 장바구니에 넣은 순서대로 정렬해주기 위해 사용.
     */
    public void createTimeMillis() {
        this.createTimeMillis = System.currentTimeMillis();
    }

    /**
     * Dto 안에 hashKey 를 담아두기 위해 BacK API 에서 generateUniqueHashKey 메서드를 사용해서 넣어줌.
     *
     * @param hashKey       redis hashKey
     */
    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    /**
     * Front 에서 옵션에 대해 수정이 일어나면 hashKey 를 새롭게 생성.
     * 이전에 있던 key 와 hashKey 는 삭제하고
     * 변겨된 옵션에 대해서 새롭게 메뉴:옵션s 로 hashKey 생성
     *
     * @param hashKey       redis hashKey
     */
    public void setRefreshHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    /**
     * 수량이 100보다 작으면 증가, 100 초과이면 100 유지.
     *
     */
    public void incrementCount() {

        if (count <= 100) {
            count++;
        }
    }

    /**
     * 수량이 1보다 크면 감소, 1 미만이면 유지.
     */
    public void decrementCount() {

        if (count > 1) {
            count--;
        }
    }
}
